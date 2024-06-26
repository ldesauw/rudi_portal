package org.rudi.facet.generator.pdf.util;

/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for the signature / timestamp examples.
 * 
 * @author Tilman Hausherr
 */

@Slf4j
public class SignatureUtils {
	private SignatureUtils() {
	}

	/**
	 * Get the access permissions granted for this document in the DocMDP transform parameters dictionary. Details are described in the table "Entries in
	 * the DocMDP transform parameters dictionary" in the PDF specification.
	 *
	 * @param doc document.
	 * @return the permission value. 0 means no DocMDP transform parameters dictionary exists. Other return values are 1, 2 or 3. 2 is also returned if
	 *         the DocMDP transform parameters dictionary is found but did not contain a /P entry, or if the value is outside the valid range.
	 */
	public static int getMDPPermission(PDDocument doc) {
		COSBase base = doc.getDocumentCatalog().getCOSObject().getDictionaryObject(COSName.PERMS);
		if (base instanceof COSDictionary) {
			COSDictionary permsDict = (COSDictionary) base;
			base = permsDict.getDictionaryObject(COSName.DOCMDP);
			if (base instanceof COSDictionary) {
				COSDictionary signatureDict = (COSDictionary) base;
				base = signatureDict.getDictionaryObject(COSName.REFERENCE);
				if (base instanceof COSArray) {
					COSArray refArray = (COSArray) base;
					return getMDPPermission(refArray);
				}
			}
		}
		return 0;
	}

	private static int getMDPPermission(COSArray refArray) {
		COSBase base;
		for (int i = 0; i < refArray.size(); ++i) {
			base = refArray.getObject(i);
			if (base instanceof COSDictionary) {
				int permission = getMDPPermissionFromCOSDictionnary(base);
				if (permission != 0) {
					return permission;
				}
			}
		}
		return 0;
	}

	private static int getMDPPermissionFromCOSDictionnary(COSBase base) {
		COSDictionary sigRefDict = (COSDictionary) base;
		if (COSName.DOCMDP.equals(sigRefDict.getDictionaryObject(COSName.TRANSFORM_METHOD))) {
			base = sigRefDict.getDictionaryObject(COSName.TRANSFORM_PARAMS);
			if (base instanceof COSDictionary) {
				COSDictionary transformDict = (COSDictionary) base;
				int accessPermissions = transformDict.getInt(COSName.P, 2);
				if (accessPermissions < 1 || accessPermissions > 3) {
					accessPermissions = 2;
				}
				return accessPermissions;
			}
		}
		return 0;
	}

	/**
	 * Set the "modification detection and prevention" permissions granted for this document in the DocMDP transform parameters dictionary. Details are
	 * described in the table "Entries in the DocMDP transform parameters dictionary" in the PDF specification.
	 *
	 * @param doc               The document.
	 * @param signature         The signature object.
	 * @param accessPermissions The permission value (1, 2 or 3).
	 *
	 * @throws IOException if a signature exists.
	 */
	public static void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions)
			throws IOException {
		for (PDSignature sig : doc.getSignatureDictionaries()) {
			// "Approval signatures shall follow the certification signature if one is present"
			// thus we don't care about timestamp signatures
			if (COSName.DOC_TIME_STAMP.equals(sig.getCOSObject().getItem(COSName.TYPE))) {
				continue;
			}
			if (sig.getCOSObject().containsKey(COSName.CONTENTS)) {
				throw new IOException("DocMDP transform method not allowed if an approval signature exists");
			}
		}

		COSDictionary sigDict = signature.getCOSObject();

		// DocMDP specific stuff
		COSDictionary transformParameters = new COSDictionary();
		transformParameters.setItem(COSName.TYPE, COSName.TRANSFORM_PARAMS);
		transformParameters.setInt(COSName.P, accessPermissions);
		transformParameters.setName(COSName.V, "1.2");
		transformParameters.setNeedToBeUpdated(true);

		COSDictionary referenceDict = new COSDictionary();
		referenceDict.setItem(COSName.TYPE, COSName.SIG_REF);
		referenceDict.setItem(COSName.TRANSFORM_METHOD, COSName.DOCMDP);
		referenceDict.setItem(COSName.DIGEST_METHOD, COSName.getPDFName("SHA1"));
		referenceDict.setItem(COSName.TRANSFORM_PARAMS, transformParameters);
		referenceDict.setNeedToBeUpdated(true);

		COSArray referenceArray = new COSArray();
		referenceArray.add(referenceDict);
		sigDict.setItem(COSName.REFERENCE, referenceArray);
		referenceArray.setNeedToBeUpdated(true);

		// Catalog
		COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
		COSDictionary permsDict = new COSDictionary();
		catalogDict.setItem(COSName.PERMS, permsDict);
		permsDict.setItem(COSName.DOCMDP, signature);
		catalogDict.setNeedToBeUpdated(true);
		permsDict.setNeedToBeUpdated(true);
	}

	/**
	 * Log if the certificate is not valid for signature usage. Doing this anyway results in Adobe Reader failing to validate the PDF.
	 *
	 * @param x509Certificate
	 * @throws java.security.cert.CertificateParsingException
	 */
	public static void checkCertificateUsage(X509Certificate x509Certificate) throws CertificateParsingException {
		// Check whether signer certificate is "valid for usage"
		// https://stackoverflow.com/a/52765021/535646
		// https://www.adobe.com/devnet-docs/acrobatetk/tools/DigSig/changes.html#id1
		boolean[] keyUsage = x509Certificate.getKeyUsage();
		if (keyUsage != null && !keyUsage[0] && !keyUsage[1]) {
			// (unclear what "signTransaction" is)
			// https://tools.ietf.org/html/rfc5280#section-4.2.1.3
			log.error("Certificate key usage does not include " + "digitalSignature nor nonRepudiation");
		}
		List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
		if (extendedKeyUsage != null && !extendedKeyUsage.contains(KeyPurposeId.id_kp_emailProtection.toString())
				&& !extendedKeyUsage.contains(KeyPurposeId.id_kp_codeSigning.toString())
				&& !extendedKeyUsage.contains(KeyPurposeId.anyExtendedKeyUsage.toString())
				&& !extendedKeyUsage.contains("1.2.840.113583.1.1.5") &&
				// not mentioned in Adobe document, but tolerated in practice
				!extendedKeyUsage.contains("1.3.6.1.4.1.311.10.3.12")) {
			log.error("Certificate extended key usage does not include "
					+ "emailProtection, nor codeSigning, nor anyExtendedKeyUsage, "
					+ "nor 'Adobe Authentic Documents Trust'");
		}
	}

	/**
	 * Log if the certificate is not valid for timestamping.
	 *
	 * @param x509Certificate
	 * @throws java.security.cert.CertificateParsingException
	 */
	public static void checkTimeStampCertificateUsage(X509Certificate x509Certificate)
			throws CertificateParsingException {
		List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
		// https://tools.ietf.org/html/rfc5280#section-4.2.1.12
		if (extendedKeyUsage != null && !extendedKeyUsage.contains(KeyPurposeId.id_kp_timeStamping.toString())) {
			log.error("Certificate extended key usage does not include timeStamping");
		}
	}

	/**
	 * Log if the certificate is not valid for responding.
	 *
	 * @param x509Certificate
	 * @throws java.security.cert.CertificateParsingException
	 */
	public static void checkResponderCertificateUsage(X509Certificate x509Certificate)
			throws CertificateParsingException {
		List<String> extendedKeyUsage = x509Certificate.getExtendedKeyUsage();
		// https://tools.ietf.org/html/rfc5280#section-4.2.1.12
		if (extendedKeyUsage != null && !extendedKeyUsage.contains(KeyPurposeId.id_kp_OCSPSigning.toString())) {
			log.error("Certificate extended key usage does not include OCSP responding");
		}
	}

	/**
	 * Gets the last relevant signature in the document, i.e. the one with the highest offset.
	 * 
	 * @param document to get its last signature
	 * @return last signature or null when none found
	 */
	public static PDSignature getLastRelevantSignature(PDDocument document) {
		SortedMap<Integer, PDSignature> sortedMap = new TreeMap<>();
		for (PDSignature signature : document.getSignatureDictionaries()) {
			int sigOffset = signature.getByteRange()[1];
			sortedMap.put(sigOffset, signature);
		}
		if (sortedMap.size() > 0) {
			PDSignature lastSignature = sortedMap.get(sortedMap.lastKey());
			COSBase type = lastSignature.getCOSObject().getItem(COSName.TYPE);
			if (type == null || COSName.SIG.equals(type) || COSName.DOC_TIME_STAMP.equals(type)) {
				return lastSignature;
			}
		}
		return null;
	}

	public static TimeStampToken extractTimeStampTokenFromSignerInformation(SignerInformation signerInformation)
			throws CMSException, IOException, TSPException {
		if (signerInformation.getUnsignedAttributes() == null) {
			return null;
		}
		AttributeTable unsignedAttributes = signerInformation.getUnsignedAttributes();
		// https://stackoverflow.com/questions/1647759/how-to-validate-if-a-signed-jar-contains-a-timestamp
		Attribute attribute = unsignedAttributes.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
		if (attribute == null) {
			return null;
		}
		ASN1Object obj = (ASN1Object) attribute.getAttrValues().getObjectAt(0);
		CMSSignedData signedTSTData = new CMSSignedData(obj.getEncoded());
		return new TimeStampToken(signedTSTData);
	}

	public static void validateTimestampToken(TimeStampToken timeStampToken)
			throws IOException, CertificateException, TSPException, OperatorCreationException {
		// https://stackoverflow.com/questions/42114742/
		@SuppressWarnings("unchecked") // TimeStampToken.getSID() is untyped
		Collection<X509CertificateHolder> tstMatches = timeStampToken.getCertificates()
				.getMatches(timeStampToken.getSID());
		X509CertificateHolder certificateHolder = tstMatches.iterator().next();
		SignerInformationVerifier siv = new JcaSimpleSignerInfoVerifierBuilder()
				.setProvider(SecurityProvider.getProvider()).build(certificateHolder);
		timeStampToken.validate(siv);
	}

	/**
	 * Look for gaps in the cross reference table and display warnings if any found. See also
	 * <a href="https://stackoverflow.com/questions/71267471/">here</a>.
	 *
	 * @param doc document.
	 */
	public static void checkCrossReferenceTable(PDDocument doc) {
		TreeSet<COSObjectKey> set = new TreeSet<>(doc.getDocument().getXrefTable().keySet());
		if (set.size() != set.last().getNumber()) {
			long n = 0;
			for (COSObjectKey key : set) {
				++n;
				while (n < key.getNumber()) {
					log.warn("Object " + n + " missing, signature verification may fail in "
							+ "Adobe Reader, see https://stackoverflow.com/questions/71267471/");
					++n;
				}
			}
		}
	}
}

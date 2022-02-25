package org.rudi.microservice.kalim.service.integration.impl.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
@Slf4j
public class PostIntegrationRequestTreatmentHandler extends AbstractIntegrationRequestTreatmentHandlerWithValidation {

	PostIntegrationRequestTreatmentHandler(DatasetService datasetService, APIManagerHelper apiManagerHelper, ObjectMapper objectMapper, List<AbstractMetadataValidator<?>> metadataValidators, Error500Builder error500Builder) {
		super(datasetService, apiManagerHelper, objectMapper, metadataValidators, error500Builder);
	}

	@Override
	protected void treat(IntegrationRequestEntity integrationRequest, Metadata metadata) throws DataverseAPIException, APIManagerException {
		final String doi = datasetService.createDataset(metadata);
		try {
			final Metadata metadataCreated = datasetService.getDataset(doi);
			createApi(integrationRequest, metadataCreated);
		} catch (RuntimeException e) {
			log.error("On va supprimer le JDD qui vient d'être créé car une erreur est survenue", e);
			datasetService.deleteDataset(doi);
			throw e;
		}
	}

	private void createApi(IntegrationRequestEntity integrationRequest, Metadata metadataCreated) throws DataverseAPIException, APIManagerException {
		try {
			apiManagerHelper.createAPI(integrationRequest, metadataCreated);
		} catch (final APIManagerException | RuntimeException e) {
			datasetService.deleteDataset(metadataCreated.getGlobalId());
			throw e;
		}
	}

}
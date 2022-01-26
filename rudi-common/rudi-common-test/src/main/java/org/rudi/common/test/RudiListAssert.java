package org.rudi.common.test;

import org.assertj.core.api.FactoryBasedNavigableListAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertFactory;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class RudiListAssert<E> extends FactoryBasedNavigableListAssert<ListAssert<E>, List<? extends E>, E, ObjectAssert<E>> {
	RudiListAssert(List<? extends E> actual) {
		super(actual, RudiListAssert.class, new ObjectAssertFactory<>());
	}

	/**
	 * Verifies that the actual value is equal to expected comparing JSON representations.
	 *
	 * @param path the given file path containing the JSON to compare the actual to.
	 * @return this assertion object.
	 * @throws IOException   if the JSON serializing of the actual value failed
	 * @throws JSONException if the JSON formatting failed
	 */
	// source : #isXmlEqualToContentOf
	public RudiListAssert<E> isJsonEqualToContentOf(String path) throws IOException, JSONException {
		final JsonAssert<? extends List<? extends E>> jsonAssert = new JsonAssert<>(actual);
		jsonAssert.isEqualToContentOf(path);
		return this;
	}

}

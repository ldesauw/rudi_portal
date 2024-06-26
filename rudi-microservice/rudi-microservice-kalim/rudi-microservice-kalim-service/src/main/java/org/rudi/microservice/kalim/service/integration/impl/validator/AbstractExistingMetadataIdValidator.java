package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.microservice.kalim.service.integration.impl.handlers.AbstractIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PutIntegrationRequestTreatmentHandler;

abstract class AbstractExistingMetadataIdValidator<T> extends AbstractMetadataIdValidator<T> {

	AbstractExistingMetadataIdValidator(FieldExtractor<T> fieldExtractor) {
		super(fieldExtractor);
	}

	@Override
	public boolean canBeUsedBy(AbstractIntegrationRequestTreatmentHandler handler) {
		return handler instanceof PutIntegrationRequestTreatmentHandler;
	}

	@Override
	protected boolean validationSucceedsIfDatasetAlreadyExists() {
		return true;
	}
}

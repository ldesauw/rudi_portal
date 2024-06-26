/**
 *
 */
package org.rudi.microservice.acl.service.mapper.address;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.acl.core.bean.EmailAddress;
import org.rudi.microservice.acl.storage.entity.address.EmailAddressEntity;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface EmailAddressMapper extends AbstractMapper<EmailAddressEntity, EmailAddress> {

	@Override
	@InheritInverseConfiguration
	EmailAddressEntity dtoToEntity(EmailAddress dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	EmailAddress entityToDto(EmailAddressEntity entity);

}

package org.rudi.microservice.konsent.service.mapper.treatmentversion;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.rudi.common.service.mapper.AbstractMapper;
import org.rudi.common.service.mapper.MapperUtils;
import org.rudi.microservice.konsent.core.bean.InvolvedPopulationCategory;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.InvolvedPopulationCategoryEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { MapperUtils.class })
public interface InvolvedPopulationCategoryMapper extends AbstractMapper<InvolvedPopulationCategoryEntity, InvolvedPopulationCategory> {
	@Override
	@InheritInverseConfiguration
	InvolvedPopulationCategoryEntity dtoToEntity(InvolvedPopulationCategory dto);

	/**
	 * Utilisé uniquement pour la modification d'une entité.
	 * On ignore toutes les entités filles (sinon l'id de chaque entité fille est supprimé et elles sont recréées en base)
	 */
	@Override
	@Mapping(target = "labels", ignore = true)
	void dtoToEntity(InvolvedPopulationCategory dto, @MappingTarget InvolvedPopulationCategoryEntity entity);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	InvolvedPopulationCategory entityToDto(InvolvedPopulationCategoryEntity entity);
}

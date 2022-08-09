/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service;

import org.rudi.bpmn.core.bean.FormDefinition;
import org.rudi.bpmn.core.bean.ProcessFormDefinition;
import org.rudi.bpmn.core.bean.SectionDefinition;
import org.rudi.facet.bpmn.bean.form.FormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.ProcessFormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.SectionDefinitionSearchCriteria;
import org.rudi.facet.bpmn.helper.form.ActionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.UUID;

/**
 * @author FNI18300
 */
public interface FormService {

	SectionDefinition createSectionDefinition(SectionDefinition section);

	SectionDefinition updateSectionDefinition(SectionDefinition section);

	void deleteSectionDefinition(UUID sectionUuid);

	SectionDefinition getSectionDefinition(UUID sectionUuid);

	Page<SectionDefinition> searchSectionDefinitions(SectionDefinitionSearchCriteria searchCriteria, Pageable pageable);

	FormDefinition createFormDefinition(FormDefinition form);

	FormDefinition updateFormDefinition(FormDefinition form);

	FormDefinition addSectionDefinition(UUID formUuid, UUID sectionUuid, boolean readOnly, Integer position);

	FormDefinition removeFormSectionDefinition(UUID formUuid, UUID formSectionUuid);

	void deleteFormDefinition(UUID formUuid);

	FormDefinition getFormDefinition(UUID sectionUuid);

	Page<FormDefinition> searchFormDefinitions(FormDefinitionSearchCriteria searchCriteria, Pageable pageable);

	ProcessFormDefinition createProcessFormDefinition(ProcessFormDefinition processFormDefinition);

	ProcessFormDefinition updateProcessFormDefinition(ProcessFormDefinition processFormDefinition);

	void deleteProcessFormDefinition(UUID processFormUuid);

	Page<ProcessFormDefinition> searchProcessFormDefinitions(ProcessFormDefinitionSearchCriteria searchCriteria,
			Pageable pageable);

	/**
	 * Crée ou met à jour le formulaire lié à une action dont la définition se trouve dans le fichier JSON :
	 *
	 * <pre>bpmn/form/`processDefinitionId`__`userTaskId`__`actionName`.json</pre>
	 */
	void createOrUpdateProcessFormDefinitionForAction(ActionId actionId) throws IOException;
}

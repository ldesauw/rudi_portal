package org.rudi.microservice.strukture.facade.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.rudi.common.facade.util.UtilPageable;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.exceptions.CreateUserException;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.core.bean.PagedOrganizationList;
import org.rudi.microservice.strukture.facade.controller.api.OrganizationsApi;
import org.rudi.microservice.strukture.service.organization.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrganizationsController implements OrganizationsApi {

	private final OrganizationService organizationService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_STRUKTURE_ADMINISTRATOR', 'MODULE_KALIM')")
	public ResponseEntity<Organization> createOrganization(Organization organization) throws CreateUserException {
		return ResponseEntity.ok(organizationService.createOrganization(organization));
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_STRUKTURE_ADMINISTRATOR')")
	public ResponseEntity<Void> deleteOrganization(UUID uuid) throws AppServiceNotFoundException {
		organizationService.deleteOrganization(uuid);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Organization> getOrganization(UUID uuid) throws AppServiceNotFoundException {
		return ResponseEntity.ok(organizationService.getOrganization(uuid));
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_STRUKTURE_ADMINISTRATOR', 'USER')")
	public ResponseEntity<User> getOrganizationUserFromOrganizationUuid(UUID organizationUuid) throws Exception {
		return ResponseEntity.ok(organizationService.getOrganizationUserFromOrganizationUuid(organizationUuid));
	}

	@Override
	public ResponseEntity<PagedOrganizationList> searchOrganizations(UUID uuid, String name, Boolean active, UUID userUuid, Integer offset, Integer limit, String order) {
		val searchCriteria = new OrganizationSearchCriteria()
				.uuid(uuid)
				.name(name)
				.active(active)
				.userUuid(userUuid);
		val pageable = utilPageable.getPageable(offset, limit, order);
		val page = organizationService.searchOrganizations(searchCriteria, pageable);
		return ResponseEntity.ok(new PagedOrganizationList()
				.total(page.getTotalElements())
				.elements(page.getContent()));
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_STRUKTURE_ADMINISTRATOR')")
	public ResponseEntity<Void> updateOrganization(Organization organization) throws AppServiceException {
		organizationService.updateOrganization(organization);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_STRUKTURE_ADMINISTRATOR', 'MODULE_KALIM')")
	public ResponseEntity<OrganizationMember> addOrganizationMember(UUID organizationUuid, OrganizationMember organizationMember) throws AppServiceException {
		return ResponseEntity.ok(organizationService.addOrganizationMember(organizationUuid, organizationMember));
	}

	@Override
	public ResponseEntity<List<OrganizationMember>> getOrganizationMembers(UUID organizationUuid) throws AppServiceException {
		return ResponseEntity.ok(organizationService.getOrganizationMembers(organizationUuid));
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_STRUKTURE_ADMINISTRATOR')")
	public ResponseEntity<Void> removeOrganizationMember(UUID organizationUuid, UUID userUuid) throws Exception {
		organizationService.removeOrganizationMembers(organizationUuid, userUuid);
		return ResponseEntity.noContent().build();
	}
}

package org.rudi.common.service.helper;

import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service utilitaire pour récupérer les infos sur l'utilisateur connecté.
 */
@Component
public class UtilContextHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtilContextHelper.class);

	/**
	 * Retourne l'utilisateur connecté.
	 *
	 * @return connectedUser
	 * @see #setAuthenticatedUser(AuthenticatedUser)
	 */
	@Nullable
	public AuthenticatedUser getAuthenticatedUser() {

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		AuthenticatedUser result = null;
		if (auth == null) {
			LOGGER.error("Null authentification");
		} else {
			final Object detail = auth.getDetails();
			if (detail == null) {
				LOGGER.error("User detail is null");
			} else {
				if (detail instanceof AuthenticatedUser) {
					result = (AuthenticatedUser) detail;
				} else {
					LOGGER.error("Unknown authenticated user {}", auth.getPrincipal());
				}
			}

		}
		return result;
	}

	/**
	 * @see #getAuthenticatedUser()
	 */
	public void setAuthenticatedUser(AuthenticatedUser authenticatedUser) {
		final var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				authenticatedUser.getLogin(), null, collectAuthoritiesFromRoles(authenticatedUser.getRoles())
		);
		usernamePasswordAuthenticationToken.setDetails(authenticatedUser);
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	}

	private Collection<? extends GrantedAuthority> collectAuthoritiesFromRoles(List<String> roles) {
		List<SimpleGrantedAuthority> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(roles)) {
			for (String role : roles) {
				result.add(new SimpleGrantedAuthority(role));
			}
		}
		return result;
	}

	public boolean hasRole(String roleName) {
		val authenticatedUser = getAuthenticatedUser();
		return hasRole(authenticatedUser, roleName);
	}

	public boolean hasRole(AuthenticatedUser authenticatedUser, String roleName) {
		boolean result = false;
		if (authenticatedUser != null && CollectionUtils.isNotEmpty(authenticatedUser.getRoles())) {
			result = authenticatedUser.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase(roleName));
		}
		return result;
	}
}

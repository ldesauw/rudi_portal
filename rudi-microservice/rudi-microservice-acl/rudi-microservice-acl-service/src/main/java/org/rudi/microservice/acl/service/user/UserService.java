/**
 * 
 */
package org.rudi.microservice.acl.service.user;

import java.util.List;
import java.util.UUID;

import javax.net.ssl.SSLException;
import javax.validation.Valid;

import org.rudi.microservice.acl.core.bean.AbstractAddress;
import org.rudi.microservice.acl.core.bean.ClientKey;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service de gestion des utilisateurs Rudi
 * 
 * @author FNI18300
 *
 */
public interface UserService {

	/**
	 * Charge la liste paginée des utilisateurs en fonction de critères de recherche
	 * 
	 * @param searchCriteria
	 * @param pageable
	 * @return liste paginée des utilisateurs
	 */
	Page<User> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * Retourne un utilisateur en fonction de son uuid, avec toutes ses propriétés chargées
	 * 
	 * @param uuid
	 * @return
	 */
	User getUser(UUID uuid);

	/**
	 * Retourne l'utilisateur connecté
	 * 
	 * @return
	 */
	public User getMe();

	/**
	 * Retourn un utilisateur par son login
	 * 
	 * @param login        le login
	 * @param withPassword pour avoir le password ou non
	 * @return
	 */
	User getUserByLogin(String login, boolean withPassword);

	/**
	 * Create a user
	 * 
	 * @param user
	 * @return
	 */
	User createUser(User user);

	/**
	 * Update a User entity
	 * 
	 * @param user
	 * @return
	 */
	User updateUser(User user);

	/**
	 * Delete a User entity
	 * 
	 * @param uuid
	 */
	void deleteUser(UUID uuid);

	/**
	 * Retourne une adresse d'un utilisateur
	 * 
	 * @param userUuid
	 * @param addressUuid
	 * @return
	 */
	AbstractAddress getAddress(UUID userUuid, UUID addressUuid);

	/**
	 * Retourne les adresses d'un utilisateur
	 * 
	 * @param userUuid
	 * @return
	 */
	List<AbstractAddress> getAddresses(UUID userUuid);

	/**
	 * Ajoute une adresse sur un utilisateur
	 * 
	 * @param userUuid
	 * @param abstractAddress
	 * @return
	 */
	AbstractAddress createAddress(UUID userUuid, AbstractAddress abstractAddress);

	/**
	 * Modifie une adresse d'un utilisateur
	 * 
	 * @param userUuid
	 * @param abstractAddress
	 * @return
	 */
	AbstractAddress updateAddress(UUID userUuid, @Valid AbstractAddress abstractAddress);

	/**
	 * Supprime une adresse d'un utilisateur
	 * 
	 * @param userUuid
	 * @param addressUuid
	 */
	void deleteAddress(UUID userUuid, UUID addressUuid);

	/**
	 * Récupération des clés WSO2 d'un utilisateur
	 * @param login				login de l'utilisateur
	 * @return					ClientKey
	 * @throws SSLException		Erreur lors de la récupération des clés
	 */
	ClientKey getClientKeyByLogin(String login) throws SSLException;

}
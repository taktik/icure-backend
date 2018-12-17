/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.logic;

import org.taktik.icure.constants.Users;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.Role;
import org.taktik.icure.entities.User;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.exceptions.UserRegistrationException;
import org.taktik.icure.logic.listeners.UserLogicListener;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserLogic extends EntityPersister<User, String>, PrincipalLogic<User> {

	/**
	 * Retrieve properties or the given user
	 */
	Set<Property> getProperties(String userId);

	/**
	 * Modify properties for the given user
	 *
	 * @param userId        The target user
	 * @param newProperties The new properties to set.
	 */
	void modifyProperties(String userId, Set<Property> newProperties);

	User newUser(Users.Type type, String login, String password, String healthcarePartyId) throws CreationException;

	User registerUser(User user, String password) throws UserRegistrationException;

	User createUser(User user) throws MissingRequirementsException;

	User registerUser(String email, String password, String healthcarePartyId, String name) throws UserRegistrationException;

	boolean isLoginValid(String login);

	boolean isPasswordValid(String password);

	User modifyUser(User modifiedUser);

	void modifyUserAttributes(String userId, Map<String, Object> attributesValues);

	/**
	 * Disables the given user. Resets its password.
	 *
	 * @param userId
	 */
	void enableUser(String userId);

	void disableUser(String userId);

	String encodePassword(String password);

	boolean isUserActive(String userId);

	boolean verifyPasswordToken(String userId, String token);

	boolean verifyActivationToken(String userId, String token);

	boolean usePasswordToken(String userId, String token, String newPassword);

	boolean useActivationToken(String userId, String token);

	void checkUsersExpiration();

	List<User> getExpiredUsers(Instant fromExpirationDate, Instant toExpirationDate);

	void acceptUserTermsOfUse(String userId);

	void addListener(UserLogicListener listener);

	void removeListener(UserLogicListener listener);

	void addPermissions(String userId, Set<Permission> permissions);

	void modifyPermissions(String userId, Set<Permission> permissions);

	void modifyRoles(String userId, Set<Role> roles);

	User getUser(String id);

	List<User> getUsersByLogin(String login);

	User getUserByLogin(String login);

	User getUserByEmail(String email);

	User newUser(Users.Type type, Users.Status status, String login, Instant createdDate);

	void deleteUser(User user);

	void undeleteUser(User user);

	User buildStandardUser(String userName, String password);

	User getBootstrapUser();

	void deleteUser(String id);

	void undeleteUser(String id);

	Collection<Role> getRoles(User user);

	void save(User user);

	void userLogged(User user);

	PaginatedList<User> listUsers(PaginationOffset pagination);

	User setProperties(User user, List<Property> properties);

	List<User> getUsers(List<String> ids);

	User getUserOnFallbackDb(String userId);

	User getUserOnUserDb(String userId, String groupId);

	User findUserOnUserDb(String userId, String groupId);

	List<User> getUsersByPartialIdOnFallbackDb(String id);

	List<User> findUsersByLoginOnFallbackDb(String username);

	List<String> findByHcpartyId(String hcpartyId);
}

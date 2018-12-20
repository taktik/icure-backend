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

package org.taktik.icure.logic.impl;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.constants.TypedValuesType;
import org.taktik.icure.constants.Users;
import org.taktik.icure.dao.UserDAO;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.PropertyType;
import org.taktik.icure.entities.Role;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.entities.embed.TypedValue;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.exceptions.UserRegistrationException;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PropertyLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.logic.listeners.UserLogicListener;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Transactional
@org.springframework.stereotype.Service
public class UserLogicImpl extends PrincipalLogicImpl<User> implements UserLogic {
	private static final Logger log = LoggerFactory.getLogger(UserLogicImpl.class);

	private static final PropertyUtilsBean pub = new PropertyUtilsBean();


	private static final Duration CHECK_USERS_EXPIRATION_TIME_RANGE = Duration.ofDays(1);

	private UserDAO userDAO;

	private HealthcarePartyLogic healthcarePartyLogic;
	private PropertyLogic propertyLogic;

	//	private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
	private PasswordEncoder passwordEncoder;


	private Set<UserLogicListener> listeners = new HashSet<>();

	private UUIDGenerator uuidGenerator;

	public UserLogicImpl() {
	}

	public void init() {
	}

	@Override
	public void addListener(UserLogicListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(UserLogicListener listener) {
		listeners.remove(listener);
	}

	@Override
	public User getUser(String id) {
		return fillGroup(userDAO.get(id));
	}

	private User fillGroup(User user) {
		if (user == null) { return null; }

		user.setGroupId(null);
		return user;
	}

	@Override
	public User getUserByEmail(String email) {
		List<User> byEmail = userDAO.findByEmail(email);

		if (byEmail.size() == 0) {
			return null;
		}
		if (byEmail.size() > 1) {
			throw new IllegalStateException("Two users with same email " + email);
		}

		return fillGroup(byEmail.get(0));
	}

	@Override
	public List<String> findByHcpartyId(String hcpartyId) {
		return userDAO.findByHcpId(hcpartyId).parallelStream()
				.filter(v -> v != null)
				.map(v -> v.getId())
//				.map(v -> new LabelledOccurence((String) v.getKey().getComponents().get(1), v.getValue()))
//				.sorted(Comparator.comparing(LabelledOccurence::getOccurence).reversed())
				.collect(Collectors.toList());
	}

	@Override
	public User newUser(Users.Type type, Users.Status status, String email, Instant createdDate) {
		User user = new User();

		initUser(type, status, createdDate, user);

		user.setLogin(email);
		user.setEmail(email);

		fillDefaultProperties(user);

		return userDAO.create(user);
	}

	private void initUser(Users.Type type, Users.Status status, Instant createdDate, User user) {
		if (user.getId() == null) {
			user.setId(uuidGenerator.newGUID().toString());
		}

		user.setType(type);
		user.setStatus(status);
		user.setCreatedDate(createdDate);
	}

	private void fillDefaultProperties(User user) {
		user.getProperties().add(
				new Property(new PropertyType(TypedValuesType.JSON, "org.taktik.icure.datafilters"),
						new TypedValue(TypedValuesType.JSON, "{\"label\":{\"en\":\"Lab results\"},\"tags\":[{\"CD-ITEM\":\"labresult\"}]}")));

		user.getProperties().add(
				new Property(new PropertyType(TypedValuesType.JSON, "org.taktik.icure.preferred.forms"),
						new TypedValue(TypedValuesType.JSON, "{\"org.taktik.icure.form.standard.medicalhistory\":\"FFFFFFFF-FFFF-FFFF-FFFF-DOSSMED00000\",\"org.taktik.icure.form.standard.consultation\":\"FFFFFFFF-FFFF-FFFF-FFFF-CONSULTATION\"}")));

		user.getProperties().add(
				new Property(new PropertyType(TypedValuesType.JSON, "org.taktik.icure.tarification.favorites"),
						new TypedValue(TypedValuesType.JSON, "{}")));

	}


	@Override
	public User buildStandardUser(String userName, String password) {
		User user = new User();

		user.setType(Users.Type.database);
		user.setStatus(Users.Status.ACTIVE);
		user.setName(userName);
		user.setLogin(userName);
		user.setCreatedDate(Instant.now());
		user.setPasswordHash(encodePassword(password));
		user.setEmail(userName);

		fillDefaultProperties(user);

		return user;
	}

	@Override
	public User getBootstrapUser() {
		User user = this.buildStandardUser("bootstrap", "bootstrap");
		user.setId("bootstrap");
		return user;
	}


	public void deleteUser(String userId) {
		User user = getUser(userId);
		if (user != null) {
			deleteUser(user);
		}
	}

	public void undeleteUser(String userId) {
		User user = getUser(userId);
		if (user != null) {
			undeleteUser(user);
		}
	}

	@Override
	public Collection<Role> getRoles(User user) {
		return getParents(user);
	}

	@Override
	public List<User> getUsersByLogin(String login) {
		return userDAO.findByUsername(formatLogin(login)).stream().map(this::fillGroup).collect(Collectors.toList());
	}

	public User getUserByLogin(String login) {
		// Format login
		login = formatLogin(login);

		List<User> byUsername = userDAO.findByUsername(login);

		if (byUsername.size() == 0) {
			return null;
		}

		return fillGroup(byUsername.get(0));
	}

	@Override
	public User newUser(@NotNull Users.Type type, @NotNull String email, @NotNull String password, String healthcarePartyId) throws CreationException {
		// Format login
		email = formatLogin(email);
		Validate.isTrue(isLoginValid(email), "Login is invalid");

		if (type != null && type.equals(Users.Type.database)) {
			Validate.isTrue(isPasswordValid(password), "Password is invalid");
		}

		User user = getUserByLogin(email);
		if (user != null) {
			throw new CreationException("User already exists");
		}

		// Create user
		user = newUser(type, Users.Status.ACTIVE, email, Instant.now());

		setHealthcarePartyIdIfExists(healthcarePartyId, user);

		// Set password if any
		if (password != null) {
			user.setPasswordHash(encodePassword(password));
		}

		return userDAO.create(user);
	}

	private void setHealthcarePartyIdIfExists(String healthcarePartyId, User user) {
		if (healthcarePartyId != null) {
			HealthcareParty healthcareParty = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);
			if (healthcareParty != null) {
				user.setHealthcarePartyId(healthcarePartyId);
			} else {
				log.error("newUser: healthcare party " + healthcarePartyId + "does not exist. But, user is created with Null healthcare party.");
			}
		}
	}

	@Override
	public User registerUser(User user, String password) throws UserRegistrationException {
		if (propertyLogic.getSystemPropertyValue(PropertyTypes.System.USER_REGISTRATION_ENABLED.getIdentifier()) != null && propertyLogic.<Boolean>getSystemPropertyValue(PropertyTypes.System.USER_REGISTRATION_ENABLED.getIdentifier())) {
			checkArgument(user != null, "Email cannot be null.");
			checkArgument(password != null, "Password cannot be null.");

			if (!isLoginValid(user.getLogin())) {
				throw new UserRegistrationException("Login is invalid");
			}
			// Check password
			if (!isPasswordValid(password)) {
				throw new UserRegistrationException("Password is invalid");
			}
			// Check login does not already exist
			if (getUserByLogin(user.getLogin()) != null) {
				throw new UserRegistrationException("User login already exists");
			}

			initUser(Users.Type.database, Users.Status.ACTIVE, Instant.now(), user);

			user.setPasswordHash(encodePassword(password));

			fillDefaultProperties(user);

			// Save user
			userDAO.create(user);

			// Notify listeners
			for (UserLogicListener listener : listeners) {
				listener.userRegistered(user);
			}

			return user;
		}
		return null;
	}

	@Override
	public User createUser(User user) throws MissingRequirementsException {
		// checking requirements
		if (user == null || (user.getLogin() != null) || (user.getEmail() == null)) {
			throw new MissingRequirementsException("createUser: Requirements are not met. Email has to be set and the Login has to be null.");
		}
		List<User> createdUsers = new ArrayList<>(1);
		try {
			// check whether user exists
			User userByEmail = this.getUserByEmail(user.getEmail());
			if (userByEmail != null) {
				throw new CreationException("User already exists (" + user.getEmail() + ")");
			}

			user.setId(uuidGenerator.newGUID().toString());
			user.setCreatedDate(Instant.now());
			user.setLogin(user.getEmail());

			this.createEntities(Collections.singleton(user), createdUsers);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid User", e);
		}
		return createdUsers.size() == 0 ? null : createdUsers.get(0);
	}


	@Override
	public User registerUser(String email, String password, String healthcarePartyId, String name) throws UserRegistrationException {
		if (propertyLogic.getSystemPropertyValue(PropertyTypes.System.USER_REGISTRATION_ENABLED.getIdentifier()) != null && propertyLogic.<Boolean>getSystemPropertyValue(PropertyTypes.System.USER_REGISTRATION_ENABLED.getIdentifier())) {
			checkArgument(email != null, "Email cannot be null.");
			checkArgument(password != null, "Password cannot be null.");

			User user = newUser(Users.Type.database, Users.Status.ACTIVE, formatLogin(email), Instant.now());
			user.setEmail(email);
			user.setName(name);

			setHealthcarePartyIdIfExists(healthcarePartyId, user);

			return registerUser(user,password);
		}

		return null;
	}

	@Override
	public boolean isUserActive(String userId) {
		User user = getUser(userId);
		if (user != null) {
			// Check user is ACTIVE
			if (user.getStatus() == null || !user.getStatus().equals(Users.Status.ACTIVE)) {
				return false;
			}

			// Check expirationDate
			return user.getExpirationDate() == null || !user.getExpirationDate().isBefore(Instant.now());
		}

		return false;
	}

	@Override
	public boolean verifyPasswordToken(String userId, String token) {
		User user = getUser(userId);
		if (user != null) {
			if (user.getPasswordToken() != null && token != null) {
				if (user.getPasswordToken().equals(token)) {
					return user.getPasswordTokenExpirationDate() == null || user.getPasswordTokenExpirationDate().isAfter(Instant.now());
				}
			}
		}

		return false;
	}

	@Override
	public boolean verifyActivationToken(String userId, String token) {
		if (token != null) {
			User user = getUser(userId);
			if (user != null) {
				if (user.getActivationToken() != null && user.getActivationToken().equals(token)) {
					return user.getActivationTokenExpirationDate() == null || user.getActivationTokenExpirationDate().isAfter(Instant.now());
				}
			}
		}

		return false;
	}

	@Override
	public boolean usePasswordToken(String userId, String token, String newPassword) {
		// Validate token
		if (verifyPasswordToken(userId, token)) {
			// Validate new password
			if (isPasswordValid(newPassword)) {
				// Get user
				User user = getUser(userId);

				// Set new password
				user.setPasswordHash(encodePassword(newPassword));

				// Remove passwordToken and passwordTokenExpirationDate
				user.setPasswordToken(null);
				user.setPasswordTokenExpirationDate(null);

				// Notify listeners
				for (UserLogicListener listener : listeners) {
					listener.userResetPassword(user);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean useActivationToken(String userId, String token) {
		// Validate token
		if (verifyActivationToken(userId, token)) {
			// Get user
			User user = getUser(userId);

			// Set user ACTIVE
			user.setStatus(Users.Status.ACTIVE);

			// Remove expirationDate
			user.setExpirationDate(null);

			// Remove activationToken and activationTokenExpirationDate
			user.setActivationToken(null);
			user.setActivationTokenExpirationDate(null);

			// Notify listeners
			for (UserLogicListener listener : listeners) {
				listener.userActivated(user);
			}

			return true;
		}

		return false;
	}

	@Override
	public List<User> getExpiredUsers(Instant fromExpirationDate, Instant toExpirationDate) {
		return userDAO.getExpiredUsers(fromExpirationDate, toExpirationDate);
	}

	@Override
	public void acceptUserTermsOfUse(String userId) {
		User user = getUser(userId);
		if (user != null) {
			user.setTermsOfUseDate(Instant.now());
		}
	}

	private String formatLogin(String login) {
		if (login != null) {
			// Trim login
			login = login.trim();
		}

		return login;
	}

	@Override
	public boolean isLoginValid(String login) {
		if (login == null || login.isEmpty()) {
			return false;
		}

		// Check for regular expression
		String loginRegexp = propertyLogic.getSystemPropertyValue(PropertyTypes.System.USER_LOGIN_REGEXP.getIdentifier());
		if (loginRegexp != null && !loginRegexp.isEmpty()) {
			return Pattern.matches(loginRegexp, login);
		}

		return true;
	}

	@Override
	public boolean isPasswordValid(String password) {
		if (password == null || password.isEmpty()) {
			return false;
		}

		// Check for regular expression
		String passwordRegexp = propertyLogic.getSystemPropertyValue(PropertyTypes.System.USER_PASSWORD_REGEXP.getIdentifier());
		if (passwordRegexp != null && !passwordRegexp.isEmpty()) {
			return Pattern.matches(passwordRegexp, password);
		}

		return true;
	}

	@Override
	public User modifyUser(User modifiedUser) {
		if (modifiedUser.getPasswordHash() != null && !modifiedUser.getPasswordHash().matches("^[0-9a-zA-Z]{64}$")) {
			modifiedUser.setPasswordHash(encodePassword(modifiedUser.getPasswordHash()));
		}

		// Save user
		userDAO.save(modifiedUser);
		return getUser(modifiedUser.getId());
	}

	@Override
	public void addPermissions(String userId, Set<Permission> permissions) {
		User user = getUser(userId);
		if (user != null) {
			// Add permissions
			user.getPermissions().addAll(permissions);

			// Modify user
			modifyUser(user);
		}
	}

	@Override
	public void modifyPermissions(String userId, Set<Permission> permissions) {
		User user = getUser(userId);
		if (user != null) {
			// Set permissions
			user.setPermissions(permissions);

			// Modify user
			modifyUser(user);
		}
	}

	@Override
	public void modifyRoles(String userId, Set<Role> roles) {
		User user = getUser(userId);
		if (user != null) {
			// Set roles
			HashSet<String> ids = new HashSet<>();
			for (Role role : roles) {
				ids.add(role.getId());
			}
			user.setRoles(ids);

			// Modify user
			modifyUser(user);
		}
	}

	@Override
	public void modifyUserAttributes(String userId, Map<String, Object> attributesValues) {
		User targetUser = getUser(userId);
		if (targetUser != null) {
			for (String attribute : attributesValues.keySet()) {
				try {
					pub.setProperty(targetUser, attribute, attributesValues.get(attribute));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					log.error("Exception", e);
				}
			}
		}

		// Save user
		userDAO.save(targetUser);
	}

	@Override
	public void deleteUser(User user) {
		user = getUser(user.getId());
		if (user != null) {
			// Delete user
			userDAO.remove(user);
		}
	}

	@Override
	public void undeleteUser(User user) {
		user = getUser(user.getId());
		if (user != null) {
			// Delete user
			userDAO.unremove(user);
		}
	}

	@Override
	public void disableUser(String userId) {
		User user = getUser(userId);
		if (user != null) {
			user.setStatus(Users.Status.DISABLED);
		}
		userDAO.save(user);
	}

	@Override
	public void enableUser(String userId) {
		User user = getUser(userId);
		if (user != null) {
			user.setStatus(Users.Status.ACTIVE);
		}
		userDAO.save(user);
	}

	@Override
	public Set<Property> getProperties(String userId) {
		return getProperties(userId, true, true, true);
	}

	@Override
	public void modifyProperties(String userId, Set<Property> propertiesToModify) {
		User user = getUser(userId);
		if (user != null) {
			Validate.notNull(propertiesToModify);
			Set<Property> existingProperties = user.getProperties();
			if (existingProperties == null) {
				user.setProperties(propertiesToModify);
			} else {
				Set<Property> newProperties = Sets.newHashSet(propertiesToModify);
				for (final Property existingProp : existingProperties) {
					if (propertiesToModify.stream().noneMatch(candidateProperty -> existingProp.getType().equals(candidateProperty.getType()))) {
						newProperties.add(existingProp);
					}
				}
				user.setProperties(newProperties);
			}
			userDAO.save(user);
		}
	}

	@Override
	public boolean createEntities(Collection<User> users, Collection<User> createdUsers) {
		for (User user : users) {
			fillDefaultProperties(user);
			if (user.getPasswordHash() != null && !user.getPasswordHash().matches("^[0-9a-zA-Z]{64}$")) {
				user.setPasswordHash(encodePassword(user.getPasswordHash()));
			}
			createdUsers.add(userDAO.create(user));
		}
		return true;
	}

	@Override
	public List<User> updateEntities(Collection<User> users) {
		return users.stream().map(this::modifyUser).map(this::fillGroup).collect(Collectors.toList());
	}

	@Override
	public void deleteEntities(Collection<String> userIds) {
		for (String userId : userIds) {
			deleteUser(userId);
		}
	}

	@Override
	public void undeleteEntities(Collection<String> userIds) {
		for (String userId : userIds) {
			undeleteUser(userId);
		}
	}

	@Override
	public List<User> getAllEntities() {
		return userDAO.getAll().stream().map(this::fillGroup).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllEntityIds() {
		return userDAO.getAll().stream().map(StoredDocument::getId).collect(Collectors.toList());
	}

	@Override
	public boolean hasEntities() {
		return userDAO.hasAny();
	}

	@Override
	public boolean exists(String id) {
		return userDAO.contains(id);
	}

	@Override
	public User getEntity(String id) {
		return fillGroup(getUser(id));
	}

	@Override
	public void checkUsersExpiration() {
		Instant toExpirationDate = Instant.now();
		Instant fromExpirationDate = toExpirationDate.minus(CHECK_USERS_EXPIRATION_TIME_RANGE);

		// Get users that expired in this time range
		List<User> expiredUsers = userDAO.getExpiredUsers(fromExpirationDate, toExpirationDate);
		for (User user : expiredUsers) {
			// Notify listeners
			for (UserLogicListener listener : listeners) {
				listener.userExpired(user);
			}
		}
	}

	@Override
	public void save(User user) {
		userDAO.save(user);
	}

	@Override
	public void userLogged(User user) {
		user = getUser(user.getId());

		// Set new last login date
		user.setLastLoginDate(Instant.now());

		// Notify user logic listeners
		for (UserLogicListener listener : listeners) {
			listener.userLogged(user);
		}

		// Save any changes made to the user
		userDAO.save(user);
	}

	@Override
	public PaginatedList<User> listUsers(PaginationOffset pagination) {
		PaginatedList<User> userPaginatedList = userDAO.listUsers(pagination);

		userPaginatedList.setRows(userPaginatedList.getRows().stream().map(this::fillGroup).collect(Collectors.toList()));

		return userPaginatedList;
	}


	@Override
	public User setProperties(User user, List<Property> properties) {
		for (Property p : properties) {
			Optional<Property> prop = user.getProperties().stream().filter(pp -> pp.getType().getIdentifier().equals(p.getType().getIdentifier())).findAny();

			if (!prop.isPresent()) {
				user.getProperties().add(new Property(new PropertyType(p.getType().getType(), p.getType().getIdentifier()), new TypedValue(p.getType().getType(), p.getValue())));
			} else {
				prop.orElseThrow(IllegalStateException::new).setTypedValue(new TypedValue(p.getType().getType(), p.getValue()));
			}
		}

		return this.modifyUser(user);
	}

	@Override
	public List<User> getUsers(List<String> ids) {
		return userDAO.getList(ids).stream().map(this::fillGroup).collect(Collectors.toList());
	}

	@Override
	public User getUserOnFallbackDb(String userId) {
		return userDAO.getOnFallback(userId);
	}

	@Override
	public User getUserOnUserDb(String userId, String groupId) {
		return fillGroup(userDAO.getUserOnUserDb(userId, groupId, false));
	}

	@Override
	public User findUserOnUserDb(String userId, String groupId) {
		return fillGroup(userDAO.findUserOnUserDb(userId, groupId, false));
	}

	@Override
	public List<User> getUsersByPartialIdOnFallbackDb(String id) {
		return userDAO.getUsersByPartialIdOnFallback(id);
	}

	@Override
	public List<User> findUsersByLoginOnFallbackDb(String login) {
		// Format login
		login = formatLogin(login);
		return  userDAO.findByUsernameOnFallback(login);
	}

	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Override
	public User getPrincipal(String userId) {
		return userId == null ? null : userId.equals("bootstrap") ? getBootstrapUser() : getUser(userId);
	}

	@Override
	public String encodePassword(String password) {
		return passwordEncoder.encodePassword(password, null);
	}

	@Autowired
	public void setPropertyLogic(PropertyLogic propertyLogic) {
		this.propertyLogic = propertyLogic;
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

}

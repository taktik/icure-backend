/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.taktik.icure.constants.Users;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.entities.Role;
import org.taktik.icure.entities.User;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.logic.RoleLogic;
import org.taktik.icure.logic.UserLogic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExternalUserMapper {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected UserLogic userLogic;
	protected RoleLogic roleLogic;

	protected boolean allowUserTypeConversion = true;

	protected Set<String> neededGroups;
	protected Map<String, String> groupsMapping;
	protected boolean groupsToRolesMapping = false;

	protected Set<String> defaultRoles;
	protected boolean flushPreviousRoles = true;
	protected boolean flushPreviousPermissions = true;

	public User mapUser(Users.Type userType, String username, Set<String> groups, Map<String, Object> userAttributesValues) {
		// Check for needed groups
		if (neededGroups != null && !neededGroups.isEmpty()) {
			for (String neededGroup : neededGroups) {
				if (groups == null || !groups.contains(neededGroup)) {
					throw new AuthenticationServiceException("Needed group \"" + neededGroup + "\" not matched !");
				}
			}
		}

		// Retrieving user from DB or creating it
		User user = userLogic.getUserByLogin(username);
		if (user == null) {
			try {
				user = userLogic.newUser(userType, username, null, null);
			} catch (CreationException e) {
				log.error(e.getMessage(),e);
			}
		} else {
			// Make sure user's status is active
			user.setStatus(Users.Status.ACTIVE);

			// Convert user to correct type if needed and allowed
			if (!user.getType().equals(userType)) {
				if (allowUserTypeConversion) {
					user.setType(userType);
					user.setPasswordHash(null);
				} else {
					throw new IllegalAccessError();
				}
			}

			// Modify user
			userLogic.modifyUser(user);
		}

		// Update attribute values
		if (userAttributesValues != null && !userAttributesValues.isEmpty()) {
			userLogic.modifyUserAttributes(user.getId(), userAttributesValues);
		}

		// Map user's groups to roleNames
		Set<String> roleNames = new HashSet<String>();
		if (defaultRoles != null) {
			roleNames.addAll(defaultRoles);
		}
		if (groupsMapping != null) {
			for (Map.Entry<String, String> groupMapping : groupsMapping.entrySet()) {
				if (groups != null && groups.contains(groupMapping.getKey())) {
					roleNames.add(groupMapping.getValue());
				}
			}
		}
		if (groupsToRolesMapping) {
			if (groups != null) {
				roleNames.addAll(groups);
			}
		}
		log.debug("Groups mapped for user : " + roleNames);

		// Update the user's roles
		Set<Role> roles = new HashSet<Role>();
		if (!flushPreviousRoles) {
			if (user.getRoles() != null) {
				roles.addAll(userLogic.getRoles(user));
			}
		}
		for (String roleName : roleNames) {
			Role role = roleLogic.getRoleByName(roleName);
			if (role != null) {
				roles.add(role);
			}
		}
		userLogic.modifyRoles(user.getId(), roles);
		log.debug("Updated roles of user successfully : {}", roles);

		// Update the user's permissions
		Set<Permission> permissions = new HashSet<Permission>();
		if (!flushPreviousPermissions) {
			if (user.getPermissions() != null) {
				permissions.addAll(user.getPermissions());
			}
		}
		userLogic.modifyPermissions(user.getId(), permissions);

		return user;
	}

	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Autowired
	public void setRoleLogic(RoleLogic roleLogic) {
		this.roleLogic = roleLogic;
	}

	public void setAllowUserTypeConversion(boolean allowUserTypeConversion) {
		this.allowUserTypeConversion = allowUserTypeConversion;
	}

	public void setNeededGroups(Set<String> neededGroups) {
		this.neededGroups = neededGroups;
	}

	public void setGroupsMapping(Map<String, String> groupsMapping) {
		this.groupsMapping = groupsMapping;
	}

	public void setGroupsToRolesMapping(boolean groupsToRolesMapping) {
		this.groupsToRolesMapping = groupsToRolesMapping;
	}

	public void setDefaultRoles(Set<String> defaultRoles) {
		this.defaultRoles = defaultRoles;
	}

	public void setFlushPreviousRoles(boolean flushPreviousRoles) {
		this.flushPreviousRoles = flushPreviousRoles;
	}

	public void setFlushPreviousPermissions(boolean flushPreviousPermissions) {
		this.flushPreviousPermissions = flushPreviousPermissions;
	}
}

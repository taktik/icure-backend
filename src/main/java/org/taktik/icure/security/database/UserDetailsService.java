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

package org.taktik.icure.security.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.taktik.icure.constants.Users;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.PermissionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.security.PermissionSet;
import org.taktik.icure.security.PermissionSetIdentifier;
import org.taktik.icure.security.UserDetails;

import java.util.HashSet;
import java.util.Set;


public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
	private static final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

	private UserLogic userLogic;
	private PermissionLogic permissionLogic;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		boolean isToken = username.matches("(.+:)?[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}");

		User user = isToken ? userLogic.getUser(username):userLogic.getUserByLogin(username);

		if (user != null && user.getGroupId()!=null) {
			user = userLogic.getUserOnUserDb(user.getId().contains(":") ? user.getId().split(":")[1] : user.getId(), user.getGroupId());
		}

		// Check user exists, is not disabled and is of DATABASE type
		if (user == null) {
			throw new UsernameNotFoundException(username);
		} else if (!userLogic.isUserActive(user.getId())) {
			throw new UsernameNotFoundException(username);
		} else if (!user.getType().equals(Users.Type.database)) {
			throw new UsernameNotFoundException(username);
		}
		log.debug("Authentication of '" + username + "' in progress ..");

		// Build permissionSetIdentifier
		PermissionSetIdentifier permissionSetIdentifier = new PermissionSetIdentifier(User.class, user.getId());

		PermissionSet permissionSet = permissionLogic.getPermissionSet(permissionSetIdentifier);
		Set<GrantedAuthority> authorities = permissionSet == null ? new HashSet<>() : permissionSet.getGrantedAuthorities();

		log.debug("Authorities: " + authorities);

		DatabaseUserDetails userDetails = new DatabaseUserDetails(permissionSetIdentifier, authorities, user.getPasswordHash(), user.getSecret(), user.isUse2fa());

		userDetails.setRev(user.getRev());
		userDetails.setApplicationTokens(user.getApplicationTokens());

		return userDetails;
	}

	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Autowired
	public void setPermissionLogic(PermissionLogic permissionLogic) {
		this.permissionLogic = permissionLogic;
	}
}

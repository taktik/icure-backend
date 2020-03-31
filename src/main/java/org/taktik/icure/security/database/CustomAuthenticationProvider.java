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

import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import org.taktik.icure.entities.Group;
import org.taktik.icure.constants.Users;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.logic.GroupLogic;
import org.taktik.icure.logic.PermissionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.properties.AuthenticationProperties;
import org.taktik.icure.security.PermissionSet;
import org.taktik.icure.security.PermissionSetIdentifier;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
	private AuthenticationProperties authenticationProperties;

	public CustomAuthenticationProvider(UserLogic userLogic, GroupLogic groupLogic, PermissionLogic permissionLogic) {
		this.userLogic = userLogic;
		this.groupLogic = groupLogic;
		this.permissionLogic = permissionLogic;
	}

	private UserLogic userLogic;
	private GroupLogic groupLogic;
	private PermissionLogic permissionLogic;

	private boolean isPasswordValid(User u, String password) {
		if (u.getApplicationTokens().containsValue(password)) { return true; }
		if (u.isUse2fa() != null && u.isUse2fa() && !u.isSecretEmpty()) {
			String[] splittedPassword = password.split("\\|");
			return getPasswordEncoder().isPasswordValid(u.getPasswordHash(), splittedPassword[0], null);
		} else {
			return getPasswordEncoder().isPasswordValid(u.getPasswordHash(), password, null);
		}
	}

	/**
	 * Authenticate users from global user db
	 * @param auth
	 * @return
	 */
	@Override
	public Authentication authenticate(Authentication auth) {
		if (auth.getPrincipal() == null) { throw new BadCredentialsException("Invalid username or password"); }

		Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, auth,
				messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.onlySupports",
						"Only UsernamePasswordAuthenticationToken is supported"));


		String username = auth.getName();
		boolean isFullToken = username.matches("(.+/)([0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}|idUser_idLogin_.+)");
		boolean isPartialToken = username.matches("[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}|idUser_idLogin_.+");

		List<User> users = (
				isFullToken ? Collections.singletonList(userLogic.getUserOnFallbackDb(username.replace('/', ':'))) :
				isPartialToken ? userLogic.getUsersByPartialIdOnFallbackDb(username) :
						userLogic.findUsersByLoginOnFallbackDb(username)
		).stream().filter(u -> u != null && u.getStatus() == Users.Status.ACTIVE).sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());

		User user = null;
		String groupId = null;
		Group group = null;

		List<User> matchingUsers = new LinkedList<>();

		String password = auth.getCredentials().toString();
		for (User userOnFallbackDb : users) {
			String userId = userOnFallbackDb.getId().contains(":") ? userOnFallbackDb.getId().split(":")[1] : userOnFallbackDb.getId();
			String gId = userOnFallbackDb.getGroupId();

			if (gId != null || authenticationProperties.getLocal()) {
				Group g = gId == null ? null : groupLogic.findGroup(gId);
				User candidate = userLogic.findUserOnUserDb(userId, gId, g != null ? g.dbInstanceUrl() : null);
				if (candidate != null && (this.isPasswordValid(candidate, auth.getCredentials().toString()))) {

					if (groupId == null && gId != null) {
						user = candidate;
						groupId = gId;
						group = g;
					}
					matchingUsers.add(userOnFallbackDb);
				} else {
					logger.warn("No match for " + userOnFallbackDb.getId() + ":" + gId);
				}
			} else {
				logger.warn("No group for " + userOnFallbackDb.getId() );
			}
		}
		if ((user == null)) {
			logger.warn("Invalid username or password for user "+username+", no user matched out of "+users.size()+" candidates");
			throw new BadCredentialsException("Invalid username or password");
		}
		if (user.isUse2fa() != null && (user.isUse2fa() != null && user.isUse2fa()) && !user.isSecretEmpty() && !user.getApplicationTokens().containsValue(password)) {
			String[] splittedPassword = password.split("\\|");
			if (splittedPassword.length<2) {
				throw new BadCredentialsException("Missing verfication code");
			}
			String verificationCode = splittedPassword[1];

			Totp totp = new Totp(user.getSecret());
			if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
				throw new BadCredentialsException("Invalid verfication code");
			}
		}

		// Build permissionSetIdentifier
		PermissionSetIdentifier permissionSetIdentifier = new PermissionSetIdentifier(User.class, user.getId());

		PermissionSet permissionSet = permissionLogic.getPermissionSet(permissionSetIdentifier);
		Set<GrantedAuthority> authorities = permissionSet == null ? new HashSet<>() : permissionSet.getGrantedAuthorities();

		DatabaseUserDetails userDetails = new DatabaseUserDetails(permissionSetIdentifier, authorities, user.getPasswordHash(), user.getSecret(), user.isUse2fa());
		if (group != null) {
			userDetails.setDbInstanceUrl(group.dbInstanceUrl());
		}
		userDetails.setGroupId(groupId);
		userDetails.setRev(user.getRev());
		userDetails.setApplicationTokens(user.getApplicationTokens());
		userDetails.setGroupIdUserIdMatching(matchingUsers.stream().map(StoredDocument::getId).collect(Collectors.toList()));

		getPreAuthenticationChecks().check(userDetails);

		for (Map.Entry<String,String> token:user.getApplicationTokens().entrySet()) {
			if (token.getValue().equals(auth.getCredentials())) {
				userDetails.setApplication(token.getKey());
			}
		}

		if (userDetails.getApplication() == null) {
			additionalAuthenticationChecks(userDetails,
					(UsernamePasswordAuthenticationToken) auth);
		}
		getPostAuthenticationChecks().check(userDetails);

		Object principalToReturn = userDetails;

		if (isForcePrincipalAsString()) {
			principalToReturn = userDetails.getUsername();
		}

		return createSuccessAuthentication(principalToReturn, auth, userDetails);
	}

	private boolean isValidLong(String code) {
		try {
			Long.parseLong(code);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	protected void doAfterPropertiesSet() throws Exception {
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Autowired
	public void setAuthenticationProperties(AuthenticationProperties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	@Autowired
	public void setGroupLogic(GroupLogic groupLogic) {
		this.groupLogic = groupLogic;
	}
}

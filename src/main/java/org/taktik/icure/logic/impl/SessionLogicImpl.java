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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.constants.TypedValuesType;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.PropertyType;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.PropertyLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.security.PermissionSetIdentifier;
import org.taktik.icure.security.UserDetails;
import org.taktik.icure.security.database.DatabaseUserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@Transactional
@org.springframework.stereotype.Service
public class SessionLogicImpl implements ICureSessionLogic {
	private static final Logger log = LoggerFactory.getLogger(SessionLogicImpl.class);

	public static final String SESSION_LOCALE_ATTRIBUTE = "locale";
	public static final String SELECTED_LOCALE_REQUEST_PARAMETER_NAME = "locale";

	private AuthenticationManager authenticationManager;

	private UserLogic userLogic;
	private PropertyLogic propertyLogic;

	private InheritableThreadLocal<SessionContext> currentSessionContext = new InheritableThreadLocal<>();

	public SessionLogicImpl() {
	}

	/* Static methods */

	private static HttpSession getCurrentHttpSession() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
			HttpServletRequest httpRequest = servletRequestAttributes.getRequest();
			if (httpRequest != null) {
				return httpRequest.getSession();
			}
		}

		return null;
	}

	private static Authentication getCurrentAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			return context.getAuthentication();
		}

		return null;
	}

	private static void setCurrentAuthentication(Authentication authentication) {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			context.setAuthentication(authentication);
		}
	}

	private static UserDetails extractUserDetails(Authentication authentication) {
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				return (UserDetails) principal;
			}
		}

		return null;
	}

	/* Generic */

	private String determineLocale(User user, HttpServletRequest httpRequest, String authLocale) {

		// Retrieve the preferred locale of the user if any
		Set<Property> properties = user.getProperties();
		PropertyType propertyTypeLocale = new PropertyType(TypedValuesType.STRING, PropertyTypes.Preference.LOCALE.getIdentifier());
		String preferredLocale = null;
		if (properties != null && propertyTypeLocale != null) {
			for (Property property : properties) {
				if (property.getType() != null && property.getType().getIdentifier() != null && property.getType().getIdentifier().equals(propertyTypeLocale.getIdentifier())) {

					preferredLocale = property.getValue();
					break;
				}
			}
		}

		// Retrieve the locale from inherited roles of the user if any
		String heritedLocale = null;
		Set<Property> heritedProperties = userLogic.getProperties(user.getId(), false, true, true);
		if (heritedProperties != null && propertyTypeLocale != null) {
			for (Property property : heritedProperties) {
				if (property.getType().getIdentifier().equals(propertyTypeLocale.getIdentifier())) {
					heritedLocale = property.getValue();
					break;
				}
			}
		}

		// If locale selectable
		List<String> localeIdentifiers = new ArrayList<>();
			localeIdentifiers.add(preferredLocale);
			localeIdentifiers.add(heritedLocale);

		// Determine the best locale to use
		List<String> validLocaleIdentifiers = null;
		return (validLocaleIdentifiers != null && !validLocaleIdentifiers.isEmpty()) ? validLocaleIdentifiers.get(0) : null;
	}

	@Override
	public HttpSession getOrCreateSession() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
			HttpServletRequest httpRequest = servletRequestAttributes.getRequest();
			if (httpRequest != null) {
				return httpRequest.getSession(true);
			}
		}

		return null;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpRequest, Authentication authentication) {
	}

	@Override
	public SessionContext login(String username, String password) {
		try {
			// Try to authenticate using given username and password
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			Authentication authentication = authenticationManager.authenticate(token);

			// Clear any previous session context
			setCurrentSessionContext(null);

			// Set current authentication
			setCurrentAuthentication(authentication);

			// Check if authentication succeeded
			if (authentication != null && authentication.isAuthenticated()) {
				HttpServletRequest httpRequest = null;
				RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
				if (requestAttributes instanceof ServletRequestAttributes) {
					ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
					httpRequest = servletRequestAttributes.getRequest();
				}
				onAuthenticationSuccess(httpRequest, authentication);
			}
			return getSessionContext(authentication);
		} catch (AuthenticationException e) {
			// Ignore
		}
		return null;
	}

	@Override
	public void logout() {
		// Remove current session context
		setCurrentSessionContext(null);
	}

	@Override
	public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		String logoutURL = propertyLogic.getSystemPropertyValue(PropertyTypes.System.AUTH_URL_LOGOUT.getIdentifier());

		// Invalidate session
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession != null) {
			try {
				httpSession.invalidate();
			} catch (IllegalStateException e) {
				log.error("Exception", e);
			}
		}

		// Get SessionContext
		SessionContext sessionContext = getCurrentSessionContext();

		// Get UserDetails
		UserDetails userDetails = sessionContext.getUserDetails();

		// Determine logout URL
		if (userDetails != null && userDetails.getLogoutURL() != null && !userDetails.getLogoutURL().isEmpty()) {
			logoutURL = userDetails.getLogoutURL();
		}

		if (logoutURL != null && !logoutURL.isEmpty()) {
			// Check for relative path
			if (logoutURL.charAt(0) == '/') {
				logoutURL = logoutURL.substring(1);
			}

			// Redirect to logout URL
			httpResponse.sendRedirect(logoutURL);
		}
	}

	/* SessionContext related */

	@Override
	public SessionContext getSessionContext(Authentication authentication) {
		return new SessionContextImpl(authentication);
	}

	@Override
	public @NotNull
	SessionContext getCurrentSessionContext() {
		SessionContext sessionContext = currentSessionContext.get();
		if (sessionContext != null) {
			return sessionContext;
		}

		Authentication authentication = getCurrentAuthentication();
		return new SessionContextImpl(authentication);
	}

	@Override
	public void resetCurrentSessionContext() {
		setCurrentSessionContext(null);
	}

	@Override
	public void setCurrentSessionContext(SessionContext sessionContext) {
		// Set the current sessionContext
		currentSessionContext.set(sessionContext);

		// Set current authentication
		setCurrentAuthentication((sessionContext != null) ? sessionContext.getAuthentication() : null);
	}

	@Override
	public <T> T doInSessionContext(SessionContext sessionContext, Callable<T> callable) throws Exception {
		// Backup current sessionContext and authentication if any
		SessionContext previousSessionContext = currentSessionContext.get();
		Authentication previousAuthentication = getCurrentAuthentication();

		// Set new sessionContext
		setCurrentSessionContext(sessionContext);

		// Prepare result
		T result = null;

		// Call callable
		if (callable != null) {
			result = callable.call();
		}

		// Restore previous sessionContext and authentication
		currentSessionContext.set(previousSessionContext);
		setCurrentAuthentication(previousAuthentication);

		return result;
	}

	@Override
	public String getCurrentUserId() {
		return getCurrentSessionContext().getUser().getId();
	}

	@Override
	public String getCurrentHealthcarePartyId() {
		return getCurrentSessionContext().getUser().getHealthcarePartyId();
	}

	private class SessionContextImpl implements SessionContext {
		private Authentication authentication;
		private UserDetails userDetails;
		private PermissionSetIdentifier permissionSetIdentifier;

		private SessionContextImpl(Authentication authentication) {
			this.authentication = authentication;
			this.userDetails = extractUserDetails(authentication);
			this.permissionSetIdentifier = (userDetails != null) ? userDetails.getPermissionSetIdentifier() : null;
		}

		@Override
		public Authentication getAuthentication() {
			return authentication;
		}

		@Override
		public UserDetails getUserDetails() {
			return userDetails;
		}

		@Override
		public boolean isAuthenticated() {
			return authentication != null && authentication.isAuthenticated();
		}

		@Override
		public boolean isAnonymous() {
			return false;
		}

		@Override
		public User getUser() {

			if (userDetails != null) {
				String userId = getUserId();
				String groupId = getGroupId();
				if (groupId != null && userId != null) {
					User u = userLogic.getUserOnUserDb(userId, groupId, getDbInstanceUrl());
					u.setGroupId(groupId);
					return u;
				}
			}

			String userId = getGroupIdUserId();

			if (userId != null) {
				return userLogic.getUserOnFallbackDb(userId);
			}
			return null;
		}

		@Override
		public String getDbInstanceUrl() {
			return userDetails == null ? null : ((DatabaseUserDetails) userDetails).getDbInstanceUrl();
		}

		@Override
		public String getGroupIdUserId() {
			String userId = getUserId();
			if (userDetails == null) {
				return userId;
			}

			String groupId = getGroupId();
			return groupId != null ? groupId +  ":" + userId : userId;
		}

		@Override
		public String getGroupId() {
			return userDetails == null ? null : ((DatabaseUserDetails) userDetails).getGroupId();
		}

		public String getUserId() {
			return (permissionSetIdentifier != null) ? permissionSetIdentifier.getPrincipalIdOfClass(User.class) : null;
		}


		@Override
		public String getLocale() {
			HttpSession httpSession = getCurrentHttpSession();
			if (httpSession != null) {
				Object sessionLocaleAttribute = httpSession.getAttribute(SESSION_LOCALE_ATTRIBUTE);
				if (sessionLocaleAttribute instanceof String) {
					return (String) sessionLocaleAttribute;
				}
			}
			return null;
		}

		@Override
		public void setLocale(String locale) {
			HttpSession httpSession = getCurrentHttpSession();
			if (httpSession != null) {
				httpSession.setAttribute(SESSION_LOCALE_ATTRIBUTE, locale);
			}
		}

		@Override
		public String[] getLocaleIdentifiers() {
			return new String[]{getLocale(), null};
		}

	}

	@Autowired
	public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Autowired
	public void setUserLogic(@Lazy UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Autowired
	public void setPropertyLogic(@Lazy PropertyLogic propertyLogic) {
		this.propertyLogic = propertyLogic;
	}
}

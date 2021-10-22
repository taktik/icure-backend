/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.security.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.TextEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UsernamePasswordAuthenticationFilter extends org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter {
	public static final String LAST_USERNAME = "lastUsername";

	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AuthenticationException {
		// Save last username in session
		String username = obtainUsername(httpRequest);
		username = (username != null) ? username.trim() : "";
		httpRequest.getSession().setAttribute(LAST_USERNAME, TextEscapeUtils.escapeEntities(username));

		return super.attemptAuthentication(httpRequest, httpResponse);
	}
}

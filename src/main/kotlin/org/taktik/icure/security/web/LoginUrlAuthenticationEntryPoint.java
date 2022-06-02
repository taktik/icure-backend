/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.security.web;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class LoginUrlAuthenticationEntryPoint extends org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint {
	Map<String, String> prefixedLoginUrls;

	public LoginUrlAuthenticationEntryPoint(String loginFormUrl, Map<String, String> prefixedLoginUrls) {
		super(loginFormUrl);
		this.prefixedLoginUrls = prefixedLoginUrls;
	}

	@Override
	protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
		String urlToLoginPage = super.buildRedirectUrlToLoginPage(request, response, authException);

		for (Map.Entry<String, String> e : this.prefixedLoginUrls.entrySet()) {
			if (request.getRequestURI().startsWith(e.getKey())) {
				urlToLoginPage = urlToLoginPage + e.getValue();
				break;
			}
		}

		return urlToLoginPage;
	}

}

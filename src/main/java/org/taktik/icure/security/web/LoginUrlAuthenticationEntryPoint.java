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

package org.taktik.icure.security.web;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class LoginUrlAuthenticationEntryPoint extends org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint {
	Map<String,String> prefixedLoginUrls;

	public LoginUrlAuthenticationEntryPoint(String loginFormUrl, Map<String, String> prefixedLoginUrls) {
		super(loginFormUrl);
		this.prefixedLoginUrls = prefixedLoginUrls;
	}

	@Override
	protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
		String urlToLoginPage = super.buildRedirectUrlToLoginPage(request, response, authException);

		for (Map.Entry<String, String> e : this.prefixedLoginUrls.entrySet()) {
			if (request.getRequestURI().startsWith(e.getKey())) { urlToLoginPage = urlToLoginPage + e.getValue(); break; }
		}

		return urlToLoginPage;
	}

}

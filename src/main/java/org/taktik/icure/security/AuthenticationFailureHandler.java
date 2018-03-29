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

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	public static final String FAILURE_URL = "failureURL";

	@Override
	public void onAuthenticationFailure(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException exception) throws IOException, ServletException {
		String failureURL = (String) httpRequest.getSession().getAttribute(FAILURE_URL);

		if (failureURL == null) {
			super.onAuthenticationFailure(httpRequest, httpResponse, exception);
		} else {
			saveException(httpRequest, exception);
			logger.debug("Redirecting to " + failureURL);
			httpResponse.sendRedirect(failureURL);
		}
	}
}
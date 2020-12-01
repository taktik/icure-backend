/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
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

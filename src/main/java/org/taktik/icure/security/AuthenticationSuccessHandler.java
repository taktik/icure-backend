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

package org.taktik.icure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.taktik.icure.asynclogic.SessionLogic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Transactional
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	protected SessionLogic sessionLogic;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Authentication authentication) throws ServletException, IOException {
		super.onAuthenticationSuccess(httpRequest, httpResponse, authentication);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		/* Get target URL */
		String target = super.determineTargetUrl(httpRequest, httpResponse);

		/* Replace using optional destination */
		String destination = httpRequest.getParameter("destination");
		if (destination != null) {
			target = destination;
		}

		/* Add optional queryString */
		String queryString = httpRequest.getParameter("queryString");
		if (queryString != null) {
			target += queryString;
		}

		return target;
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
}

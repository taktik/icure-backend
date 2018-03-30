/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.SessionLogic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Transactional
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	protected ICureSessionLogic sessionLogic;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Authentication authentication) throws ServletException, IOException {
		sessionLogic.onAuthenticationSuccess(httpRequest, authentication);
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
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
}
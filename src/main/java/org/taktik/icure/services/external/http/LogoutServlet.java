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

package org.taktik.icure.services.external.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.logic.SessionLogic;

public class LogoutServlet extends AbstractHttpServlet {
	protected SessionLogic sessionLogic;

	@Override
	protected void handleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		// Make sure HTTP request is decoded in UTF-8
		httpRequest.setCharacterEncoding("UTF-8");

		// Logout
		sessionLogic.logout(httpRequest, httpResponse);
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
}

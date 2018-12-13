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

package org.taktik.icure.logic;

import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketSession;
import org.taktik.icure.entities.User;
import org.taktik.icure.security.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.Callable;

public interface SessionLogic {

	/* Generic */
	void onAuthenticationSuccess(HttpServletRequest httpRequest, Authentication authentication);

	SessionContext login(String username, String password);

	void logout();

	void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException;

	/* SessionContext related */

	SessionContext getSessionContext(Authentication authentication);

	@NotNull
    SessionContext getCurrentSessionContext();

	void resetCurrentSessionContext();

	void setCurrentSessionContext(SessionContext sessionContext);

	<T> T doInSessionContext(SessionContext sessionContext, Callable<T> callable) throws Exception;

	interface SessionContext {
		Authentication getAuthentication();

		UserDetails getUserDetails();

		boolean isAuthenticated();

		boolean isAnonymous();

		User getUser();

		String getUserId();

		String getLocale();

		void setLocale(String locale);

		String[] getLocaleIdentifiers();

	}
}

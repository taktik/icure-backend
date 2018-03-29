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

package org.taktik.icure.security.database;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.taktik.icure.logic.impl.PatientLogicImpl;

public class ApplicationTokensUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	private static final Logger log = LoggerFactory.getLogger(ApplicationTokensUserDetailsAuthenticationProvider.class);
	private UserDetailsService userDetailsService;


	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		DatabaseUserDetails loadedUser;
		loadedUser = (DatabaseUserDetails) this.userDetailsService.loadUserByUsername(username);
		if (authentication.getCredentials() != null) {
			String presentedPassword = authentication.getCredentials().toString();

			log.debug("Comparing {} to {} for app token authentication",presentedPassword);

			for (Map.Entry<String,String> token:loadedUser.getApplicationTokens().entrySet()) {
				log.debug("Comparing {} to {} for app token authentication",presentedPassword, token.getValue());
				if (token.getValue().equals(presentedPassword)) {
					loadedUser.setApplication(token.getKey());
					return loadedUser;
				}
			}
			log.error("Authentication failed for {} (rev: {}) and pass {} (compared to: {}) in app token authentication",username,loadedUser.getRev(),presentedPassword, loadedUser.getApplicationTokens());
		} else {
			log.error("Authentication failed for  {} in app token authentication due to empty credentials", username);
		}

		throw new UsernameNotFoundException("Could not find user for this id/token combination");
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}

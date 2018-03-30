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

package org.taktik.icure.security.database;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication auth) {
		String username = (auth.getPrincipal() == null) ? "NONE_PROVIDED"
			: auth.getName();
		UserDetails user = this.getUserDetailsService().loadUserByUsername(username);
		if ((user == null)) {
			throw new BadCredentialsException("Invalid username or password");
		}
		if (user instanceof DatabaseUserDetails && ((DatabaseUserDetails) user).isUse2fa() && ((DatabaseUserDetails) user).getSecret() != null) {
			String[] splittedPassword = auth.getCredentials().toString().split("\\|");
			if (splittedPassword.length<2) {
				throw new BadCredentialsException("Missing verfication code");
			}
			String verificationCode = splittedPassword[1];

			Totp totp = new Totp(((DatabaseUserDetails) user).getSecret());
			if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
				throw new BadCredentialsException("Invalid verfication code");
			}
		}

		Authentication result = super.authenticate(auth);
		return new UsernamePasswordAuthenticationToken(
			user, result.getCredentials(), result.getAuthorities());
	}

	private boolean isValidLong(String code) {
		try {
			Long.parseLong(code);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}

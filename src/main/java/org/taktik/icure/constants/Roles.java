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

package org.taktik.icure.constants;

public interface Roles {
	public static final String DEFAULT_ROLE_NAME = "DEFAULT";

	/* Spring Security Authorities */
	interface GrantedAuthority {
		String ROLE_ADMINISTRATOR	= "ROLE_ADMINISTRATOR";
		String ROLE_USER			= "ROLE_USER";
		String ROLE_BOOTSTRAP			= "ROLE_BOOTSTRAP";
		String ROLE_ANONYMOUS		= "ROLE_ANONYMOUS";
        String ROLE_DOCTOR		    = "ROLE_DOCTOR";
        String ROLE_HC_PARTY		= "ROLE_HC_PARTY";
	}

	enum VirtualHostDependency {
		NONE,
		DIRECT,
		FULL
	}
}
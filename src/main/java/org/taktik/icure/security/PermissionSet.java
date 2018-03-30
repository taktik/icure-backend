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

import org.springframework.security.core.GrantedAuthority;
import org.taktik.icure.entities.embed.Permission;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class PermissionSet implements Serializable {
	private static final long serialVersionUID = -8590721038649447143L;

	private PermissionSetIdentifier permissionSetIdentifier;
	private Set<Permission> permissions;
	private Set<GrantedAuthority> grantedAuthorities;

	public PermissionSet(PermissionSetIdentifier permissionSetIdentifier, Set<Permission> permissions, Set<GrantedAuthority> grantedAuthorities) {
		this.permissionSetIdentifier = permissionSetIdentifier;
		this.permissions = Collections.unmodifiableSet(permissions);
		this.grantedAuthorities = Collections.unmodifiableSet(grantedAuthorities);
	}

	public PermissionSetIdentifier getPermissionSetIdentifier() {
		return permissionSetIdentifier;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public Set<GrantedAuthority> getGrantedAuthorities() {
		return grantedAuthorities;
	}
}
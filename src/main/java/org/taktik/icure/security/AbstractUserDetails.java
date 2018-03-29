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

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class AbstractUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;

	protected PermissionSetIdentifier permissionSetIdentifier;
	protected Collection<? extends GrantedAuthority> authorities;

	protected boolean realAuth = true;
	protected String locale;
	protected String logoutURL;

	protected AbstractUserDetails(PermissionSetIdentifier permissionSetIdentifier, Collection<? extends GrantedAuthority> authorities) {
		this.permissionSetIdentifier = permissionSetIdentifier;
		this.authorities = authorities;
	}

	@Override
	public PermissionSetIdentifier getPermissionSetIdentifier() {
		return permissionSetIdentifier;
	}

	@Override
	public String getUsername() {
		return permissionSetIdentifier.getPrincipalClass().getName() + ":" + permissionSetIdentifier.getPrincipalId();
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isRealAuth() {
		return realAuth;
	}

	public void setRealAuth(boolean realAuth) {
		this.realAuth = realAuth;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String getLogoutURL() {
		return logoutURL;
	}

	public void setLogoutURL(String logoutURL) {
		this.logoutURL = logoutURL;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractUserDetails that = (AbstractUserDetails) o;

		if (authorities != null ? !authorities.equals(that.authorities) : that.authorities != null) return false;
		if (permissionSetIdentifier != null ? !permissionSetIdentifier.equals(that.permissionSetIdentifier) : that.permissionSetIdentifier != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = permissionSetIdentifier != null ? permissionSetIdentifier.hashCode() : 0;
		result = 31 * result + (authorities != null ? authorities.hashCode() : 0);
		return result;
	}
}
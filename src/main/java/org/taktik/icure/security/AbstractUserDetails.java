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

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class AbstractUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;

	protected PermissionSetIdentifier permissionSetIdentifier;
	protected Collection<? extends GrantedAuthority> authorities;

	protected boolean realAuth = true;
	protected String locale;
	protected String logoutURL;
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

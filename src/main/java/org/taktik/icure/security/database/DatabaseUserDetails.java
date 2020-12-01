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

package org.taktik.icure.security.database;

import org.springframework.security.core.GrantedAuthority;
import org.taktik.icure.security.AbstractUserDetails;
import org.taktik.icure.security.PermissionSetIdentifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabaseUserDetails extends AbstractUserDetails {
	private static final long serialVersionUID = 1L;

	private String rev;
	private String passwordHash;
	private Map<String,String> applicationTokens;
	private String application;
	private String secret;
	private boolean use2fa;

	public DatabaseUserDetails(PermissionSetIdentifier permissionSetIdentifier, Set<GrantedAuthority> authorities, String passwordHash, String secret, Boolean use2fa) {
		super(permissionSetIdentifier, authorities);
		this.passwordHash = passwordHash;
		this.secret = secret;
		this.use2fa = use2fa != null ? use2fa : false;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	public Map<String, String> getApplicationTokens() {
		return applicationTokens;
	}

	public void setApplicationTokens(Map<String, String> applicationTokens) {
		this.applicationTokens = applicationTokens;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplication() {
		return application;
	}

	public String getSecret() {
		return secret;
	}

	public boolean isUse2fa() {
		return use2fa;
	}

}

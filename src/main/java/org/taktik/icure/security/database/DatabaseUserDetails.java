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
	private String groupId;
	private List<String> groupIdUserIdMatching;

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

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupIdUserIdMatching(List<String> groupIdUserIdMatching) {
		this.groupIdUserIdMatching = groupIdUserIdMatching;
	}

	public List<String> getGroupIdUserIdMatching() {
		return groupIdUserIdMatching;
	}
}

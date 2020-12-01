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

import org.taktik.icure.entities.base.Principal;

import java.io.Serializable;

public class PermissionSetIdentifier implements Serializable {
	private static final long serialVersionUID = 8536485369723490698L;

	private Class<? extends Principal> principalClass;
	private String principalId;

	public PermissionSetIdentifier(Class<? extends Principal> principalClass, String principalId) {
		this.principalClass = principalClass;
		this.principalId = principalId;
	}

	public Class<? extends Principal> getPrincipalClass() {
		return principalClass;
	}

	public String getPrincipalId() {
		return principalId;
	}

	public String getPrincipalIdOfClass(Class<? extends Principal> principalClass) {
		return (this.principalClass == principalClass) ? getPrincipalId() : null;
	}
}

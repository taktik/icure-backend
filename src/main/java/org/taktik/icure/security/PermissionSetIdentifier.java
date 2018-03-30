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
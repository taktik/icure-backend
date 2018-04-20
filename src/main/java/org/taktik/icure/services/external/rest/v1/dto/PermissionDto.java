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

package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.constants.Permissions;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PermissionDto implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected int grant;
	protected int revoke;
	protected Set<PermissionCriterionDto> criteria = new HashSet<>();

	public void grant(Permissions.Type permissionType) {
		// Grant TYPE
		grant |= permissionType.getBitValue();

		// Make sure TYPE is not revoked
		revoke &= ~permissionType.getBitValue();
	}

    public int getGrant() {
        return grant;
    }

    public void setGrant(int grant) {
        this.grant = grant;
    }

    public int getRevoke() {
        return revoke;
    }

    public void setRevoke(int revoke) {
        this.revoke = revoke;
    }

    public Set<PermissionCriterionDto> getCriteria() {
        return criteria;
    }

    public void setCriteria(Set<PermissionCriterionDto> criteria) {
        this.criteria = criteria;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + grant;
		result = prime * result + revoke;
		result = prime * result + ((criteria == null) ? 0 : criteria.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PermissionDto other = (PermissionDto) obj;
		if (grant != other.grant)
			return false;
		if (revoke != other.revoke)
			return false;
		if (criteria == null) {
			if (other.criteria != null)
				return false;
		} else if (!criteria.equals(other.criteria))
			return false;
		return true;
	}
}
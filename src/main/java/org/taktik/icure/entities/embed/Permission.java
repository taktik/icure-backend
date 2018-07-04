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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.constants.Permissions;
import org.taktik.icure.security.PermissionSetIdentifier;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permission implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected int grant;
	protected int revoke;
	protected Set<PermissionCriterion> criteria = new HashSet<>();

    public static Permission granted(Permissions.Type... permissions) {
        Permission p = new Permission();
        for(Permissions.Type pt:permissions) { p.grant(pt); }

        return  p;
    }

	public void grant(Permissions.Type permissionType) {
		// Grant TYPE
		grant |= permissionType.getBitValue();

		// Make sure TYPE is not revoked
		revoke &= ~permissionType.getBitValue();
	}

	public void revoke(Permissions.Type permissionType) {
		// Revoke TYPE
		revoke |= permissionType.getBitValue();

		// Make sure TYPE is not granted
		grant &= ~permissionType.getBitValue();
	}

	public void keep(Permissions.Type permissionType) {
		// Make sure TYPE is not granted
		grant &= ~permissionType.getBitValue();

		// Make sure TYPE is not revoked
		revoke &= ~permissionType.getBitValue();
	}

	@JsonIgnore
    public boolean isGranted(Permissions.Type permissionType) {
		return permissionType.isEnabled(grant);
	}

    @JsonIgnore
    public boolean isRevoked(Permissions.Type permissionType) {
		return permissionType.isEnabled(revoke);
	}

    @JsonIgnore
    public boolean isKept(Permissions.Type permissionType) {
		return !isGranted(permissionType) && !isRevoked(permissionType);
	}

	public int getGrant() {
		int grant = this.grant;

		// Remove invalid grant TYPE
		for (Permissions.Type permissionType : Permissions.Type.values()) {
			if (!canBeUsedWith(permissionType)) {
				grant &= ~permissionType.getBitValue();
			}
		}

		return grant;
	}

	public int getRevoke() {
		int revoke = this.revoke;

		// Remove invalid revoke TYPE
		for (Permissions.Type permissionType : Permissions.Type.values()) {
			if (!canBeUsedWith(permissionType)) {
				revoke &= ~permissionType.getBitValue();
			}
		}

		return revoke;
	}

    protected boolean canBeUsedWith(Permissions.Type permissionType) {
		if (getCriteria() != null && !getCriteria().isEmpty()) {
			for (PermissionCriterion criterion : getCriteria()) {
				if (!permissionType.isCriterionTypeSupported(criterion.getType())) {
					return false;
				}
			}

			return true;
		}

		return permissionType.isNoCriterionSupported();
	}

	public Set<PermissionCriterion> getCriteria() {
		return criteria;
	}

	public void setGrant(int grant) {
		this.grant = grant;
	}

	public void setRevoke(int revoke) {
		this.revoke = revoke;
	}

    @JsonIgnore
    public boolean isUseless() {
		return (getGrant() == 0 && getRevoke() == 0);
	}

	public boolean conflictWith(Permission permission) {
		if (hasSameCriteriaAs(permission)) {
			return ((getGrant() & permission.getRevoke()) != 0 || (getRevoke() & permission.getGrant()) != 0);
		}

		return false;
	}

    public boolean hasNoCriteria() {
		return (criteria == null || criteria.isEmpty());
	}

    public boolean hasSameCriteriaAs(Permission permission) {
		if (criteria == null) {
			return permission.getCriteria() == null;
		}
		return permission.getCriteria() != null && criteria.equals(permission.getCriteria());
	}

	public boolean superScope(Permission lookup) {
		// TODO improve this
		return (hasNoCriteria() && !lookup.hasNoCriteria());
	}

	public void setCriteria(Set<PermissionCriterion> value) {
		this.criteria = value;
	}

	public void addToCriteria(PermissionCriterion value) {
		this.criteria.add(value);
	}

	public void removeFromCriteria(PermissionCriterion value) {
		this.criteria.remove(value);
	}

    public int countMatchedCriteria(PermissionSetIdentifier permissionSetIdentifier, String dataCreationUserId, String dataModificationUserId,
                                    String patientCreationUserId, String patientModificationUserId,
                                    String patientReferenceHcParty, Set<String> patientHcPartiesTeam,
                                    Permissions.CriterionDataType matchedDataType, String matchedPatientStatus) {
        int matched = 0;

        if (criteria != null) {
            for (PermissionCriterion criterion : criteria) {
                if (criterion.match(permissionSetIdentifier, dataCreationUserId, dataModificationUserId, patientCreationUserId, patientModificationUserId, patientReferenceHcParty, patientHcPartiesTeam, matchedDataType, matchedPatientStatus)) {
                    matched++;
                } else {
                    return -1;
                }
            }
        }

        return matched;
    }

	@Override
	public Permission clone() {
		Permission clone = new Permission();
		clone.setGrant(getGrant());
		clone.setRevoke(getRevoke());
		if (criteria != null) {
			for (PermissionCriterion criterion : criteria) {
				PermissionCriterion criterionClone = criterion.clone();
				clone.addToCriteria(criterionClone);
			}
		}
		return clone;
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
		Permission other = (Permission) obj;
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
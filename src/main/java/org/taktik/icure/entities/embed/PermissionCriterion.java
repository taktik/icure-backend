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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.constants.Permissions;
import org.taktik.icure.entities.User;
import org.taktik.icure.security.PermissionSetIdentifier;

import java.io.Serializable;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionCriterion implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected boolean negative;

	protected Permissions.CriterionTypeCurrentUser currentUser;
    protected Permissions.CriterionDataType dataType;
    protected String patientStatus;

	public Permissions.CriterionType getType() {
		if (currentUser != null) {
			return Permissions.CriterionType.CURRENT_USER;
		} else if (dataType != null) {
            return Permissions.CriterionType.DATA_TYPE;
        } else if (patientStatus != null) {
            return Permissions.CriterionType.PATIENT_STATUS;
        }

		return null;
	}

	public boolean isUseless() {
		return (currentUser == null  && dataType==null && patientStatus == null);
	}

	public boolean match(PermissionSetIdentifier permissionSetIdentifier,
                         String dataCreationUserId, String dataModificationUserId,
                         String patientCreationUserId, String patientModificationUserId,
                         String patientReferenceHcParty, Set<String> patientHcPartiesTeam,
                         Permissions.CriterionDataType matchedDataType, String matchedPatientStatus) {
		boolean match = false;

		if (currentUser != null) {
			String currentUserId = permissionSetIdentifier.getPrincipalIdOfClass(User.class);
			switch (currentUser) {
				case DATA_CREATION_USER:
					match = (dataCreationUserId != null && dataCreationUserId.equals(currentUserId));
					break;
				case DATA_MODIFICATION_USER:
					match = (dataModificationUserId != null && dataModificationUserId.equals(currentUserId));
					break;
                case PATIENT_CREATION_USER:
                    match = (patientCreationUserId != null && patientCreationUserId.equals(currentUserId));
                    break;
                case PATIENT_MODIFICATION_USER:
                    match = (patientModificationUserId != null && patientModificationUserId.equals(currentUserId));
                    break;
                case PATIENT_REFERENCE_HC_USER:
                    match = (patientReferenceHcParty != null && patientReferenceHcParty.equals(currentUserId));
                    break;
                case PATIENT_HC_TEAM_USER:
                    match = (patientHcPartiesTeam != null && patientHcPartiesTeam.contains(currentUserId));
                    break;
			}
		} else if (matchedDataType != null) {
            return matchedDataType.equals(dataType);
        } else if (matchedPatientStatus != null) {
            return matchedPatientStatus.equals(patientStatus);
        }

		if (negative) {
			match = !match;
		}

		return match;
	}

    public Permissions.CriterionTypeCurrentUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Permissions.CriterionTypeCurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public Permissions.CriterionDataType getDataType() {
        return dataType;
    }

    public void setDataType(Permissions.CriterionDataType dataType) {
        this.dataType = dataType;
    }

    public String getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    @Override
	public PermissionCriterion clone() {
		PermissionCriterion clone = new PermissionCriterion();
		clone.setNegative(isNegative());
		clone.setCurrentUser(getCurrentUser());
        clone.setDataType(getDataType());
        clone.setPatientStatus(getPatientStatus());

		return clone;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionCriterion that = (PermissionCriterion) o;

        if (negative != that.negative) return false;
        if (currentUser != that.currentUser) return false;
        if (dataType != that.dataType) return false;
        if (patientStatus != null ? !patientStatus.equals(that.patientStatus) : that.patientStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (negative ? 1 : 0);
        result = 31 * result + (currentUser != null ? currentUser.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (patientStatus != null ? patientStatus.hashCode() : 0);
        return result;
    }
}
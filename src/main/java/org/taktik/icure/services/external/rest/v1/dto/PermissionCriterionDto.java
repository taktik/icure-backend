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

package org.taktik.icure.services.external.rest.v1.dto;

import io.swagger.annotations.ApiModelProperty;
import org.taktik.icure.constants.Permissions;

import java.io.Serializable;

public class PermissionCriterionDto implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	protected boolean negative;

	protected VirtualHostDto virtualHost;
    @ApiModelProperty(dataType = "string")
    protected Permissions.CriterionTypeCurrentUser currentUser;
    @ApiModelProperty(dataType = "string")
    protected Permissions.CriterionDataType dataType;
    protected String patientStatus;

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public VirtualHostDto getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(VirtualHostDto virtualHost) {
        this.virtualHost = virtualHost;
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

    @Override
	public PermissionCriterionDto clone() {
		PermissionCriterionDto clone = new PermissionCriterionDto();
		clone.setNegative(isNegative());
		clone.setVirtualHost(getVirtualHost());
		clone.setCurrentUser(getCurrentUser());
        clone.setDataType(getDataType());
        clone.setPatientStatus(getPatientStatus());

		return clone;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionCriterionDto that = (PermissionCriterionDto) o;

        if (negative != that.negative) return false;
        if (currentUser != that.currentUser) return false;
        if (dataType != that.dataType) return false;
        if (patientStatus != null ? !patientStatus.equals(that.patientStatus) : that.patientStatus != null)
            return false;
        if (virtualHost != null ? !virtualHost.equals(that.virtualHost) : that.virtualHost != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (negative ? 1 : 0);
        result = 31 * result + (virtualHost != null ? virtualHost.hashCode() : 0);
        result = 31 * result + (currentUser != null ? currentUser.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (patientStatus != null ? patientStatus.hashCode() : 0);
        return result;
    }
}
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

package org.taktik.icure.be.ehealth.dto.civics;

import java.util.Date;

public class PharmaceuticalForm {

	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	String noInnInd;
	String dividableInd;
	String entericCoatedInd;
	String retardedInd;
	String solidToLiquid;
	String aerosolType;
	String tool;
	String vehicInd;
	String crushableInd;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCreatedTms() {
        return createdTms;
    }

    public void setCreatedTms(Date createdTms) {
        this.createdTms = createdTms;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getNameId() {
        return nameId;
    }

    public void setNameId(Long nameId) {
        this.nameId = nameId;
    }

    public String getNoInnInd() {
        return noInnInd;
    }

    public void setNoInnInd(String noInnInd) {
        this.noInnInd = noInnInd;
    }

    public String getDividableInd() {
        return dividableInd;
    }

    public void setDividableInd(String dividableInd) {
        this.dividableInd = dividableInd;
    }

    public String getEntericCoatedInd() {
        return entericCoatedInd;
    }

    public void setEntericCoatedInd(String entericCoatedInd) {
        this.entericCoatedInd = entericCoatedInd;
    }

    public String getRetardedInd() {
        return retardedInd;
    }

    public void setRetardedInd(String retardedInd) {
        this.retardedInd = retardedInd;
    }

    public String getSolidToLiquid() {
        return solidToLiquid;
    }

    public void setSolidToLiquid(String solidToLiquid) {
        this.solidToLiquid = solidToLiquid;
    }

    public String getAerosolType() {
        return aerosolType;
    }

    public void setAerosolType(String aerosolType) {
        this.aerosolType = aerosolType;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getVehicInd() {
        return vehicInd;
    }

    public void setVehicInd(String vehicInd) {
        this.vehicInd = vehicInd;
    }

    public String getCrushableInd() {
        return crushableInd;
    }

    public void setCrushableInd(String crushableInd) {
        this.crushableInd = crushableInd;
    }
}


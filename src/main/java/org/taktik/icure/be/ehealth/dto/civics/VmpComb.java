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

package org.taktik.icure.be.ehealth.dto.civics;

import java.util.Date;

import org.taktik.icure.be.ehealth.dto.civics.*;

public class VmpComb {

	org.taktik.icure.be.ehealth.dto.civics.Vmp vmp;
	org.taktik.icure.be.ehealth.dto.civics.Vmp vmpCmb;

	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	String vmpCmbSeq;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public org.taktik.icure.be.ehealth.dto.civics.Vmp getVmp() {
        return vmp;
    }

    public void setVmp(org.taktik.icure.be.ehealth.dto.civics.Vmp vmp) {
        this.vmp = vmp;
    }

    public org.taktik.icure.be.ehealth.dto.civics.Vmp getVmpCmb() {
        return vmpCmb;
    }

    public void setVmpCmb(org.taktik.icure.be.ehealth.dto.civics.Vmp vmpCmb) {
        this.vmpCmb = vmpCmb;
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

    public String getVmpCmbSeq() {
        return vmpCmbSeq;
    }

    public void setVmpCmbSeq(String vmpCmbSeq) {
        this.vmpCmbSeq = vmpCmbSeq;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


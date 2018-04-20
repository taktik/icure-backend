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

package org.taktik.icure.be.ehealth.dto.civics;

import java.util.Date;

public class HAmpIntPckComb {

	Long ampId;
	Double ampIntPckCq;
	String ampIntPckCu;
	Long ampIdCmb;
	Double ampIntPckCqCmb;
	String ampIntPckCuCmb;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	String ampIntPckCmbSeq;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmpId() {
        return ampId;
    }

    public void setAmpId(Long ampId) {
        this.ampId = ampId;
    }

    public Double getAmpIntPckCq() {
        return ampIntPckCq;
    }

    public void setAmpIntPckCq(Double ampIntPckCq) {
        this.ampIntPckCq = ampIntPckCq;
    }

    public String getAmpIntPckCu() {
        return ampIntPckCu;
    }

    public void setAmpIntPckCu(String ampIntPckCu) {
        this.ampIntPckCu = ampIntPckCu;
    }

    public Long getAmpIdCmb() {
        return ampIdCmb;
    }

    public void setAmpIdCmb(Long ampIdCmb) {
        this.ampIdCmb = ampIdCmb;
    }

    public Double getAmpIntPckCqCmb() {
        return ampIntPckCqCmb;
    }

    public void setAmpIntPckCqCmb(Double ampIntPckCqCmb) {
        this.ampIntPckCqCmb = ampIntPckCqCmb;
    }

    public String getAmpIntPckCuCmb() {
        return ampIntPckCuCmb;
    }

    public void setAmpIntPckCuCmb(String ampIntPckCuCmb) {
        this.ampIntPckCuCmb = ampIntPckCuCmb;
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

    public String getAmpIntPckCmbSeq() {
        return ampIntPckCmbSeq;
    }

    public void setAmpIntPckCmbSeq(String ampIntPckCmbSeq) {
        this.ampIntPckCmbSeq = ampIntPckCmbSeq;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


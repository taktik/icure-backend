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

public class Atc {

	String atcCv;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	String atcCvParent;
	String finalLevelInd;
	String flatRateInd;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*public Atc getParentAtc() {;
         return findByAtcCvParent(atcCvParent);
     };*/

    public String getAtcCv() {
        return atcCv;
    }

    public void setAtcCv(String atcCv) {
        this.atcCv = atcCv;
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

    public String getAtcCvParent() {
        return atcCvParent;
    }

    public void setAtcCvParent(String atcCvParent) {
        this.atcCvParent = atcCvParent;
    }

    public String getFinalLevelInd() {
        return finalLevelInd;
    }

    public void setFinalLevelInd(String finalLevelInd) {
        this.finalLevelInd = finalLevelInd;
    }

    public String getFlatRateInd() {
        return flatRateInd;
    }

    public void setFlatRateInd(String flatRateInd) {
        this.flatRateInd = flatRateInd;
    }
}


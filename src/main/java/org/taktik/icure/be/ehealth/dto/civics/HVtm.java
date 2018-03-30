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

public class HVtm {

	Long vtmId;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	String plus3Ind;
	String cheapInd;
	String whoListInd;
	String whoListRef;
	String educListInd;
	String educListGroup;
	Date initDate;
	Date closeDate;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVtmId() {
        return vtmId;
    }

    public void setVtmId(Long vtmId) {
        this.vtmId = vtmId;
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

    public String getPlus3Ind() {
        return plus3Ind;
    }

    public void setPlus3Ind(String plus3Ind) {
        this.plus3Ind = plus3Ind;
    }

    public String getCheapInd() {
        return cheapInd;
    }

    public void setCheapInd(String cheapInd) {
        this.cheapInd = cheapInd;
    }

    public String getWhoListInd() {
        return whoListInd;
    }

    public void setWhoListInd(String whoListInd) {
        this.whoListInd = whoListInd;
    }

    public String getWhoListRef() {
        return whoListRef;
    }

    public void setWhoListRef(String whoListRef) {
        this.whoListRef = whoListRef;
    }

    public String getEducListInd() {
        return educListInd;
    }

    public void setEducListInd(String educListInd) {
        this.educListInd = educListInd;
    }

    public String getEducListGroup() {
        return educListGroup;
    }

    public void setEducListGroup(String educListGroup) {
        this.educListGroup = educListGroup;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


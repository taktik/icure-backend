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

package org.taktik.icure.services.external.rest.v1.dto.be.civics;

import java.util.Date;

public class HVmp {

	Long vmpId;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	Long vtmId;
	String applicationCv;
	String sequentialInd;
	String doseFormType;
	Double administrationQuantity;
	String administrationUnit;
	Long administrationMultiplier;
	Long hyrId;
	Long noInn;
	Long noSwitch;
	Double definedDailyDoseValue;
	String definedDailyDoseUnit;
	String blackTriangleInd;
	Date initDate;
	Date closeDate;
	String wadaCv;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVmpId() {
        return vmpId;
    }

    public void setVmpId(Long vmpId) {
        this.vmpId = vmpId;
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

    public Long getVtmId() {
        return vtmId;
    }

    public void setVtmId(Long vtmId) {
        this.vtmId = vtmId;
    }

    public String getApplicationCv() {
        return applicationCv;
    }

    public void setApplicationCv(String applicationCv) {
        this.applicationCv = applicationCv;
    }

    public String getSequentialInd() {
        return sequentialInd;
    }

    public void setSequentialInd(String sequentialInd) {
        this.sequentialInd = sequentialInd;
    }

    public String getDoseFormType() {
        return doseFormType;
    }

    public void setDoseFormType(String doseFormType) {
        this.doseFormType = doseFormType;
    }

    public Double getAdministrationQuantity() {
        return administrationQuantity;
    }

    public void setAdministrationQuantity(Double administrationQuantity) {
        this.administrationQuantity = administrationQuantity;
    }

    public String getAdministrationUnit() {
        return administrationUnit;
    }

    public void setAdministrationUnit(String administrationUnit) {
        this.administrationUnit = administrationUnit;
    }

    public Long getAdministrationMultiplier() {
        return administrationMultiplier;
    }

    public void setAdministrationMultiplier(Long administrationMultiplier) {
        this.administrationMultiplier = administrationMultiplier;
    }

    public Long getHyrId() {
        return hyrId;
    }

    public void setHyrId(Long hyrId) {
        this.hyrId = hyrId;
    }

    public Long getNoInn() {
        return noInn;
    }

    public void setNoInn(Long noInn) {
        this.noInn = noInn;
    }

    public Long getNoSwitch() {
        return noSwitch;
    }

    public void setNoSwitch(Long noSwitch) {
        this.noSwitch = noSwitch;
    }

    public Double getDefinedDailyDoseValue() {
        return definedDailyDoseValue;
    }

    public void setDefinedDailyDoseValue(Double definedDailyDoseValue) {
        this.definedDailyDoseValue = definedDailyDoseValue;
    }

    public String getDefinedDailyDoseUnit() {
        return definedDailyDoseUnit;
    }

    public void setDefinedDailyDoseUnit(String definedDailyDoseUnit) {
        this.definedDailyDoseUnit = definedDailyDoseUnit;
    }

    public String getBlackTriangleInd() {
        return blackTriangleInd;
    }

    public void setBlackTriangleInd(String blackTriangleInd) {
        this.blackTriangleInd = blackTriangleInd;
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

    public String getWadaCv() {
        return wadaCv;
    }

    public void setWadaCv(String wadaCv) {
        this.wadaCv = wadaCv;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


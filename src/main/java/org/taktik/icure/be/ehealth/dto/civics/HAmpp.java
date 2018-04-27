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

public class HAmpp {

	Long amppId;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	Long ampId;
	Double contentQuantity;
	String contentUnit;
	Long vmpId;
	String treatmentDurationCatCv;
	Long contentMultiplier;
	Double totalPackSizeValue;
	Long amppIdMaxPackSize;
	String prescriptionInd;
	String socsecReimbCv;
	Long distributorId;
	Date initDate;
	Date closeDate;
	String modificationStatus;
	String cheapest;
	Date inSupply;
	Date availability;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmppId() {
        return amppId;
    }

    public void setAmppId(Long amppId) {
        this.amppId = amppId;
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

    public Long getAmpId() {
        return ampId;
    }

    public void setAmpId(Long ampId) {
        this.ampId = ampId;
    }

    public Double getContentQuantity() {
        return contentQuantity;
    }

    public void setContentQuantity(Double contentQuantity) {
        this.contentQuantity = contentQuantity;
    }

    public String getContentUnit() {
        return contentUnit;
    }

    public void setContentUnit(String contentUnit) {
        this.contentUnit = contentUnit;
    }

    public Long getVmpId() {
        return vmpId;
    }

    public void setVmpId(Long vmpId) {
        this.vmpId = vmpId;
    }

    public String getTreatmentDurationCatCv() {
        return treatmentDurationCatCv;
    }

    public void setTreatmentDurationCatCv(String treatmentDurationCatCv) {
        this.treatmentDurationCatCv = treatmentDurationCatCv;
    }

    public Long getContentMultiplier() {
        return contentMultiplier;
    }

    public void setContentMultiplier(Long contentMultiplier) {
        this.contentMultiplier = contentMultiplier;
    }

    public Double getTotalPackSizeValue() {
        return totalPackSizeValue;
    }

    public void setTotalPackSizeValue(Double totalPackSizeValue) {
        this.totalPackSizeValue = totalPackSizeValue;
    }

    public Long getAmppIdMaxPackSize() {
        return amppIdMaxPackSize;
    }

    public void setAmppIdMaxPackSize(Long amppIdMaxPackSize) {
        this.amppIdMaxPackSize = amppIdMaxPackSize;
    }

    public String getPrescriptionInd() {
        return prescriptionInd;
    }

    public void setPrescriptionInd(String prescriptionInd) {
        this.prescriptionInd = prescriptionInd;
    }

    public String getSocsecReimbCv() {
        return socsecReimbCv;
    }

    public void setSocsecReimbCv(String socsecReimbCv) {
        this.socsecReimbCv = socsecReimbCv;
    }

    public Long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Long distributorId) {
        this.distributorId = distributorId;
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

    public String getCheapest() {
        return cheapest;
    }

    public void setCheapest(String cheapest) {
        this.cheapest = cheapest;
    }

    public Date getInSupply() {
        return inSupply;
    }

    public void setInSupply(Date inSupply) {
        this.inSupply = inSupply;
    }

    public Date getAvailability() {
        return availability;
    }

    public void setAvailability(Date availability) {
        this.availability = availability;
    }
}


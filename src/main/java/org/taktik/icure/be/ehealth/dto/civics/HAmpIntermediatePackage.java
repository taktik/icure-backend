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

public class HAmpIntermediatePackage {

	Long ampId;
	Double contentQuantity;
	String contentUnit;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long addedMultiplier;
	Double addedQuantity;
	String addedUnit;
	String addedType;
	String innerPackageCv;
	String packageTxt;
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

    public Long getAddedMultiplier() {
        return addedMultiplier;
    }

    public void setAddedMultiplier(Long addedMultiplier) {
        this.addedMultiplier = addedMultiplier;
    }

    public Double getAddedQuantity() {
        return addedQuantity;
    }

    public void setAddedQuantity(Double addedQuantity) {
        this.addedQuantity = addedQuantity;
    }

    public String getAddedUnit() {
        return addedUnit;
    }

    public void setAddedUnit(String addedUnit) {
        this.addedUnit = addedUnit;
    }

    public String getAddedType() {
        return addedType;
    }

    public void setAddedType(String addedType) {
        this.addedType = addedType;
    }

    public String getInnerPackageCv() {
        return innerPackageCv;
    }

    public void setInnerPackageCv(String innerPackageCv) {
        this.innerPackageCv = innerPackageCv;
    }

    public String getPackageTxt() {
        return packageTxt;
    }

    public void setPackageTxt(String packageTxt) {
        this.packageTxt = packageTxt;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


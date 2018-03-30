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

package org.taktik.icure.services.external.rest.v1.dto.be.civics;

import java.util.Date;

public class TreatmentDurationCategory {

	String treatmentDurationCatCv;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	Long treatmentDurationValue;
	String treatmentDurationUnit;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTreatmentDurationCatCv() {
        return treatmentDurationCatCv;
    }

    public void setTreatmentDurationCatCv(String treatmentDurationCatCv) {
        this.treatmentDurationCatCv = treatmentDurationCatCv;
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

    public Long getTreatmentDurationValue() {
        return treatmentDurationValue;
    }

    public void setTreatmentDurationValue(Long treatmentDurationValue) {
        this.treatmentDurationValue = treatmentDurationValue;
    }

    public String getTreatmentDurationUnit() {
        return treatmentDurationUnit;
    }

    public void setTreatmentDurationUnit(String treatmentDurationUnit) {
        this.treatmentDurationUnit = treatmentDurationUnit;
    }
}


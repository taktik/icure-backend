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

public class Price {

	org.taktik.icure.be.ehealth.dto.civics.Ampp ampp;;

	String deliveryEnvironment;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Double priceAmnt;
	Double reimbBasePrice;
	Double referenceBasePrice;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public org.taktik.icure.be.ehealth.dto.civics.Ampp getAmpp() {
        return ampp;
    }

    public void setAmpp(org.taktik.icure.be.ehealth.dto.civics.Ampp ampp) {
        this.ampp = ampp;
    }

    public String getDeliveryEnvironment() {
        return deliveryEnvironment;
    }

    public void setDeliveryEnvironment(String deliveryEnvironment) {
        this.deliveryEnvironment = deliveryEnvironment;
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

    public Double getPriceAmnt() {
        return priceAmnt;
    }

    public void setPriceAmnt(Double priceAmnt) {
        this.priceAmnt = priceAmnt;
    }

    public Double getReimbBasePrice() {
        return reimbBasePrice;
    }

    public void setReimbBasePrice(Double reimbBasePrice) {
        this.reimbBasePrice = reimbBasePrice;
    }

    public Double getReferenceBasePrice() {
        return referenceBasePrice;
    }

    public void setReferenceBasePrice(Double referenceBasePrice) {
        this.referenceBasePrice = referenceBasePrice;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


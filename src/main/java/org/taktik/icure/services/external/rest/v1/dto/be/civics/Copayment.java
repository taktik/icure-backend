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

public class Copayment {

	Atm atm;
	Ampp ampp;

	String chapterName;
	String paragraphName;
	String deliveryEnvironment;
	Long regimeType;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Double copayAmnt;
	Double solidFlatRateAmnt;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atm getAtm() {
        return atm;
    }

    public void setAtm(Atm atm) {
        this.atm = atm;
    }

    public Ampp getAmpp() {
        return ampp;
    }

    public void setAmpp(Ampp ampp) {
        this.ampp = ampp;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getParagraphName() {
        return paragraphName;
    }

    public void setParagraphName(String paragraphName) {
        this.paragraphName = paragraphName;
    }

    public String getDeliveryEnvironment() {
        return deliveryEnvironment;
    }

    public void setDeliveryEnvironment(String deliveryEnvironment) {
        this.deliveryEnvironment = deliveryEnvironment;
    }

    public Long getRegimeType() {
        return regimeType;
    }

    public void setRegimeType(Long regimeType) {
        this.regimeType = regimeType;
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

    public Double getCopayAmnt() {
        return copayAmnt;
    }

    public void setCopayAmnt(Double copayAmnt) {
        this.copayAmnt = copayAmnt;
    }

    public Double getSolidFlatRateAmnt() {
        return solidFlatRateAmnt;
    }

    public void setSolidFlatRateAmnt(Double solidFlatRateAmnt) {
        this.solidFlatRateAmnt = solidFlatRateAmnt;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


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

public class HAmp {

	Long ampId;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	Long vmpId;
	Long atmId;
	String routeAdmCv;
	Long pharmFormId;
	String galenicFormTxt;
	Long admFormId;
	String dimensions;
	String dopCv;
	String registSpec;
	String descriptSpec;
	String duration;
	String flatRateInd;
	Long distributorId;
	Date initDate;
	Date closeDate;
	String atcCv;
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

    public Long getVmpId() {
        return vmpId;
    }

    public void setVmpId(Long vmpId) {
        this.vmpId = vmpId;
    }

    public Long getAtmId() {
        return atmId;
    }

    public void setAtmId(Long atmId) {
        this.atmId = atmId;
    }

    public String getRouteAdmCv() {
        return routeAdmCv;
    }

    public void setRouteAdmCv(String routeAdmCv) {
        this.routeAdmCv = routeAdmCv;
    }

    public Long getPharmFormId() {
        return pharmFormId;
    }

    public void setPharmFormId(Long pharmFormId) {
        this.pharmFormId = pharmFormId;
    }

    public String getGalenicFormTxt() {
        return galenicFormTxt;
    }

    public void setGalenicFormTxt(String galenicFormTxt) {
        this.galenicFormTxt = galenicFormTxt;
    }

    public Long getAdmFormId() {
        return admFormId;
    }

    public void setAdmFormId(Long admFormId) {
        this.admFormId = admFormId;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getDopCv() {
        return dopCv;
    }

    public void setDopCv(String dopCv) {
        this.dopCv = dopCv;
    }

    public String getRegistSpec() {
        return registSpec;
    }

    public void setRegistSpec(String registSpec) {
        this.registSpec = registSpec;
    }

    public String getDescriptSpec() {
        return descriptSpec;
    }

    public void setDescriptSpec(String descriptSpec) {
        this.descriptSpec = descriptSpec;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFlatRateInd() {
        return flatRateInd;
    }

    public void setFlatRateInd(String flatRateInd) {
        this.flatRateInd = flatRateInd;
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

    public String getAtcCv() {
        return atcCv;
    }

    public void setAtcCv(String atcCv) {
        this.atcCv = atcCv;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


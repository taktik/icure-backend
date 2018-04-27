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

package org.taktik.icure.services.external.rest.v1.dto.be.hub;

import be.fgov.ehealth.hubservices.core.v1.HCPartyIdType;
import be.fgov.ehealth.hubservices.core.v1.PatientIdType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTHERAPEUTICLINK;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by aduchate on 9/11/13, 11:25
 */
public class HubTherapeuticLink implements Serializable {
    protected be.fgov.ehealth.hubservices.core.v1.PatientIdType patient;
    protected be.fgov.ehealth.hubservices.core.v1.HCPartyIdType hcparty;
    protected be.fgov.ehealth.standards.kmehr.cd.v1.CDTHERAPEUTICLINK cd;
    protected Long startdate;
    protected Long enddate;
    protected String comment;

    public PatientIdType getPatient() {
        return patient;
    }

    public void setPatient(PatientIdType patient) {
        this.patient = patient;
    }

    public HCPartyIdType getHcparty() {
        return hcparty;
    }

    public void setHcparty(HCPartyIdType hcparty) {
        this.hcparty = hcparty;
    }

    public CDTHERAPEUTICLINK getCd() {
        return cd;
    }

    public void setCd(CDTHERAPEUTICLINK cd) {
        this.cd = cd;
    }

    public Long getStartdate() {
        return startdate;
    }

    public void setStartdate(Long startdate) {
        this.startdate = startdate;
    }

    public Long getEnddate() {
        return enddate;
    }

    public void setEnddate(Long enddate) {
        this.enddate = enddate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

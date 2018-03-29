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

package org.taktik.icure.be.ehealth.dto.common;

import be.fgov.ehealth.hubservices.core.v1.HCPartyIdType;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;

import java.io.Serializable;
import java.time.Instant;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 10/12/12
 * Time: 20:44
 * To change this template use File | Settings | File Templates.
 */
public class HcPartyConsent implements Serializable {
    protected be.fgov.ehealth.hubservices.core.v1.HCPartyIdType hcparty;
    protected Instant signdate;
    protected Instant revokedate;
    protected be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType author;
    protected String hubId;

    public HCPartyIdType getHcparty() {
        return hcparty;
    }

    public void setHcparty(HCPartyIdType hcparty) {
        this.hcparty = hcparty;
    }

    public Instant getSigndate() {
        return signdate;
    }

    public void setSigndate(Instant signdate) {
        this.signdate = signdate;
    }

    public Instant getRevokedate() {
        return revokedate;
    }

    public void setRevokedate(Instant revokedate) {
        this.revokedate = revokedate;
    }

    public AuthorType getAuthor() {
        return author;
    }

    public void setAuthor(AuthorType author) {
        this.author = author;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getNihii() {
        IDHCPARTYschemes scheme = hcparty != null && hcparty.getIds().size() > 0 ? hcparty.getIds().get(0).getS() : null;
        return scheme!=null && scheme.equals(IDHCPARTYschemes.ID_HCPARTY)?hcparty.getIds().get(0).getValue():null;
    }

    public String getSsin() {
        IDHCPARTYschemes scheme = hcparty != null && hcparty.getIds().size() > 0 ? hcparty.getIds().get(0).getS() : null;
        return scheme!=null && scheme.equals(IDHCPARTYschemes.INSS)?hcparty.getIds().get(0).getValue():null;
    }
}

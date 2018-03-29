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

package org.taktik.icure.services.external.rest.v1.dto.be.ehealth;

import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 10/12/12
 * Time: 20:44
 * To change this template use File | Settings | File Templates.
 */
public class HcPartyConsent implements Serializable {
    protected HcPartyId hcparty;
    protected Long signdate;
    protected Long revokedate;
    protected Author author;
    protected String hubId;

    public HcPartyId getHcparty() {
        return hcparty;
    }

    public void setHcparty(HcPartyId hcparty) {
        this.hcparty = hcparty;
    }

    public Long getSigndate() {
        return signdate;
    }

    public void setSigndate(Long signdate) {
        this.signdate = signdate;
    }

    public Long getRevokedate() {
        return revokedate;
    }

    public void setRevokedate(Long revokedate) {
        this.revokedate = revokedate;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getNihii() {
        String scheme = hcparty != null && hcparty.getIds().size() > 0 ? hcparty.getIds().get(0).getS() : null;
        return scheme!=null && scheme.equals(IDHCPARTYschemes.ID_HCPARTY.value())?hcparty.getIds().get(0).getValue():null;
    }

    public String getSsin() {
        String scheme = hcparty != null && hcparty.getIds().size() > 0 ? hcparty.getIds().get(0).getS() : null;
        return scheme!=null && scheme.equals(IDHCPARTYschemes.INSS.value())?hcparty.getIds().get(0).getValue():null;
    }
}

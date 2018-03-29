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

import java.io.Serializable;
import java.util.List;

/**
 * Created by aduchate on 9/11/13, 15:06
 */
public class Consent implements Serializable {
    protected PatientId patient;
    protected List<KmehrCd> cds;
    protected Long signdate;
    protected Long revokedate;
    protected AuthorWithPatient author;

    public PatientId getPatient() {
        return patient;
    }

    public void setPatient(PatientId patient) {
        this.patient = patient;
    }

    public List<KmehrCd> getCds() {
        return cds;
    }

    public void setCds(List<KmehrCd> cds) {
        this.cds = cds;
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

    public AuthorWithPatient getAuthor() {
        return author;
    }

    public void setAuthor(AuthorWithPatient author) {
        this.author = author;
    }
}

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

package org.taktik.icure.be.ehealth.dto.insurability;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 28/05/13
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class HospitalizedInfo implements Serializable {
    protected String hospital;
    protected Instant admissionDate;
    protected String admissionService;

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Instant getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Instant admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getAdmissionService() {
        return admissionService;
    }

    public void setAdmissionService(String admissionService) {
        this.admissionService = admissionService;
    }
}

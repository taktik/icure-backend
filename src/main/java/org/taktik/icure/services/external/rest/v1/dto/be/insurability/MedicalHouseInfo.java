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

package org.taktik.icure.services.external.rest.v1.dto.be.insurability;


import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 28/05/13
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class MedicalHouseInfo implements Serializable {
    protected Long periodStart;
    protected Long periodEnd;
    protected Boolean nurse;
    protected Boolean medical;
    protected Boolean kine;

    public Long getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Long periodStart) {
        this.periodStart = periodStart;
    }

    public Long getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Long periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Boolean isNurse() {
        return nurse;
    }

    public void setNurse(Boolean nurse) {
        this.nurse = nurse;
    }

    public Boolean isMedical() {
        return medical;
    }

    public void setMedical(Boolean medical) {
        this.medical = medical;
    }

    public Boolean isKine() {
        return kine;
    }

    public void setKine(Boolean kine) {
        this.kine = kine;
    }
}

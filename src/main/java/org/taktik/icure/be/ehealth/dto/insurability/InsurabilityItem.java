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

package org.taktik.icure.be.ehealth.dto.insurability;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 28/05/13
 * Time: 09:23
 * To change this template use File | Settings | File Templates.
 */
public class InsurabilityItem implements Serializable {

    protected String regNrWithMut;
    protected String mutuality;
    protected Period period;
    protected String ct1;
    protected String ct2;
    protected String paymentApproval;
    protected Instant insurabilityDate;

    public String getRegNrWithMut() {
        return regNrWithMut;
    }

    public void setRegNrWithMut(String regNrWithMut) {
        this.regNrWithMut = regNrWithMut;
    }

    public String getMutuality() {
        return mutuality;
    }

    public void setMutuality(String mutuality) {
        this.mutuality = mutuality;
    }


    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getCt1() {
        return ct1;
    }

    public void setCt1(String ct1) {
        this.ct1 = ct1;
    }

    public String getCt2() {
        return ct2;
    }

    public void setCt2(String ct2) {
        this.ct2 = ct2;
    }

    public String getPaymentApproval() {
        return paymentApproval;
    }

    public void setPaymentApproval(String paymentApproval) {
        this.paymentApproval = paymentApproval;
    }

    public Instant getInsurabilityDate() {
        return insurabilityDate;
    }

    public void setInsurabilityDate(Instant insurabilityDate) {
        this.insurabilityDate = insurabilityDate;
    }
}

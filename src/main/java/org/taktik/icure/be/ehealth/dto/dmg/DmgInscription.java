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

package org.taktik.icure.be.ehealth.dto.dmg;

import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 17/06/14
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public class DmgInscription extends DmgMessageWithPatient implements Serializable {
    protected Date from;
    protected Date to;

    protected Double payment1Amount;
    protected String payment1Currency;
    protected Date payment1Date;
    protected String payment1Ref;

    protected Double payment2Amount;
    protected String payment2Currency;
    protected Date payment2Date;
    protected String payment2Ref;

    private HcpartyType hcParty;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Double getPayment1Amount() {
        return payment1Amount;
    }

    public void setPayment1Amount(Double payment1Amount) {
        this.payment1Amount = payment1Amount;
    }

    public String getPayment1Currency() {
        return payment1Currency;
    }

    public void setPayment1Currency(String payment1Currency) {
        this.payment1Currency = payment1Currency;
    }

    public Date getPayment1Date() {
        return payment1Date;
    }

    public void setPayment1Date(Date payment1Date) {
        this.payment1Date = payment1Date;
    }

    public String getPayment1Ref() {
        return payment1Ref;
    }

    public void setPayment1Ref(String payment1Ref) {
        this.payment1Ref = payment1Ref;
    }

    public void setHcParty(HcpartyType hcParty) {
        this.hcParty = hcParty;
    }

    public HcpartyType getHcParty() {
        return hcParty;
    }

    public Double getPayment2Amount() {
        return payment2Amount;
    }

    public void setPayment2Amount(Double payment2Amount) {
        this.payment2Amount = payment2Amount;
    }

    public String getPayment2Currency() {
        return payment2Currency;
    }

    public void setPayment2Currency(String payment2Currency) {
        this.payment2Currency = payment2Currency;
    }

    public Date getPayment2Date() {
        return payment2Date;
    }

    public void setPayment2Date(Date payment2Date) {
        this.payment2Date = payment2Date;
    }

    public String getPayment2Ref() {
        return payment2Ref;
    }

    public void setPayment2Ref(String payment2Ref) {
        this.payment2Ref = payment2Ref;
    }

    public void setPaymentAmount(int paymentIdx, double v) {
        if (paymentIdx==1) {
            setPayment1Amount(v);
        } else {
            setPayment2Amount(v);
        }
    }

    public void setPaymentRef(int paymentIdx, String v) {
        if (paymentIdx==1) {
            setPayment1Ref(v);
        } else {
            setPayment2Ref(v);
        }
    }
    public void setPaymentDate(int paymentIdx, Date v) {
        if (paymentIdx==1) {
            setPayment1Date(v);
        } else {
            setPayment2Date(v);
        }
    }
    public void setPaymentCurrency(int paymentIdx, String v) {
        if (paymentIdx==1) {
            setPayment1Currency(v);
        } else {
            setPayment2Currency(v);
        }
    }


}

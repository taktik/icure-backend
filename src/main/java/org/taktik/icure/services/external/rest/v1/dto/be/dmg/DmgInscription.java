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

package org.taktik.icure.services.external.rest.v1.dto.be.dmg;

import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.HcParty;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 17/06/14
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
@JsonPolymorphismRoot(DmgMessage.class)
public class DmgInscription extends DmgMessage implements Serializable {
    protected String inss;
    protected String firstName;
    protected String lastName;
    protected Long birthday;
    protected Long deceased;
    protected String sex;
    protected String regNrWithMut;
    protected String mutuality;

    protected Long from;
    protected Long to;

    protected Double payment1Amount;
    protected String payment1Currency;
    protected Long payment1Date;
    protected String payment1Ref;

    protected Double payment2Amount;
    protected String payment2Currency;
    protected Long payment2Date;
    protected String payment2Ref;

    private HcParty hcParty;

    public String getInss() {
        return inss;
    }

    public void setInss(String inss) {
        this.inss = inss;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Long getDeceased() {
        return deceased;
    }

    public void setDeceased(Long deceased) {
        this.deceased = deceased;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

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

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
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

    public Long getPayment1Date() {
        return payment1Date;
    }

    public void setPayment1Date(Long payment1Date) {
        this.payment1Date = payment1Date;
    }

    public String getPayment1Ref() {
        return payment1Ref;
    }

    public void setPayment1Ref(String payment1Ref) {
        this.payment1Ref = payment1Ref;
    }

    public void setHcParty(HcParty hcParty) {
        this.hcParty = hcParty;
    }

    public HcParty getHcParty() {
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

    public Long getPayment2Date() {
        return payment2Date;
    }

    public void setPayment2Date(Long payment2Date) {
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
    public void setPaymentDate(int paymentIdx, Long v) {
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

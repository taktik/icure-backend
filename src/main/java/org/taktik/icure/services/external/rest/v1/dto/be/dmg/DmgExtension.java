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
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
@JsonPolymorphismRoot(DmgMessage.class)
public class DmgExtension extends DmgMessage implements Serializable {
    protected String inss;
    protected String firstName;
    protected String lastName;
    protected Long birthday;
    protected Long deceased;
    protected String sex;
    protected String regNrWithMut;
    protected String mutuality;

    private HcParty hcParty;
    private String claim;
    private Long encounterDate;

    public HcParty getHcParty() {
        return hcParty;
    }

    public void setHcParty(HcParty hcParty) {
        this.hcParty = hcParty;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public Long getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(Long encounterDate) {
        this.encounterDate = encounterDate;
    }

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
}

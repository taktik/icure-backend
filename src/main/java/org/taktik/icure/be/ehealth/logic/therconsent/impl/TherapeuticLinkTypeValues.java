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

package org.taktik.icure.be.ehealth.logic.therconsent.impl;

/**
 * Associations between hcparty types and therapeutic link types
 *
 * @author EHP
 *
 */

public enum TherapeuticLinkTypeValues {

    /** */
    PERSNURSE("persnurse", "nurseconsultation", "nursereferral"), //
    /** */
    PERSPHYSICIAN("persphysician", "gpconsultation", "gpreferral"), //
    /** */
    DEPTPHARMACY("deptpharmacy", "pharmacydelivery", "pharmacistreferral"), //
    /** */
    PERSPHARMACIST("perspharmacist", "pharmacydelivery", "pharmacistreferral");

    private String hcpartyType;

    private String therlinkType;

    private String referral;

    private TherapeuticLinkTypeValues(String hcpType, String therlinkType, String referral) {
        this.hcpartyType = hcpType;
        this.therlinkType = therlinkType;
        this.setReferral(referral);
    }


    /**
     * @return the hcpartyType
     */
    public String getHcpartyType() {
        return hcpartyType;
    }


    /**
     * @return the therlinkType
     */
    public String getTherlinkType() {
        return therlinkType;
    }


    /**
     * @return
     */
    public String getReferralType() {
        return referral;
    }


    /**
     * @return the referral
     */
    public String getReferral() {
        return referral;
    }


    /**
     * @param referral the referral to set
     */
    public void setReferral(String referral) {
        this.referral = referral;
    }

}

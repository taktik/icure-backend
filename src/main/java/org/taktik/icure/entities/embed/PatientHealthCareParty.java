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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by aduchate on 02/07/13, 11:59
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientHealthCareParty implements Serializable {
	private static final long serialVersionUID = 1L;

    protected PatientHealthCarePartyType type;
    protected boolean referral = false; // mark this phcp as THE active referral link (gmd)
    protected String healthcarePartyId;
    protected Map<TelecomType,String> sendFormats;  // String is in fact a UTI (uniform type identifier / a sort of super-MIME)
    protected SortedSet<ReferralPeriod> referralPeriods = new TreeSet<>(); // History of DMG ownerships

    public @Nullable String getHealthcarePartyId() {
        return healthcarePartyId;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    public Map<TelecomType, String> getSendFormats() {
        return sendFormats;
    }

    public void setSendFormats(Map<TelecomType, String> sendFormats) {
        this.sendFormats = sendFormats;
    }

    public boolean isReferral() {
		return referral;
	}

    public void setReferral(boolean referral) {
		this.referral = referral;
	}

    public SortedSet<ReferralPeriod> getReferralPeriods() {
        return referralPeriods;
    }

    public void setReferralPeriods(SortedSet<ReferralPeriod> referralPeriods) {
        this.referralPeriods = referralPeriods;
    }

    public @Nullable PatientHealthCarePartyType getType() {
        return type;
    }

    public void setType(PatientHealthCarePartyType type) {
        this.type = type;
    }
}

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

package org.taktik.icure.services.external.rest.v1.dto.embed;

/** Created by aduchate on 02/07/13, 11:59 */


import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PatientHealthCarePartyDto implements Serializable {
	private static final long serialVersionUID = 1L;

    protected PatientHealthCarePartyTypeDto type;
    @Deprecated
    protected boolean referral = false; // true if healthcarePartyId is DMG current holder/owner
    protected String healthcarePartyId;
    protected Map<TelecomType,String> sendFormats;  // String is in fact a UTI (uniform type identifier / a sort of super-MIME)
    protected List<ReferralPeriod> referralPeriods; // History of DMG ownerships

    public String getHealthcarePartyId() {
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

    public List<ReferralPeriod> getReferralPeriods() {
        return referralPeriods;
    }

    public void setReferralPeriods(List<ReferralPeriod> referralPeriods) {
        this.referralPeriods = referralPeriods;
    }

	public PatientHealthCarePartyTypeDto getType() {
		return type;
	}

	public void setType(PatientHealthCarePartyTypeDto type) {
		this.type = type;
	}


}

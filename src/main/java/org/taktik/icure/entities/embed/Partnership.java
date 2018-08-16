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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Partnership implements Serializable {
	@JsonIgnore
	private String partnershipDescription;
    private PartnershipType type;
    private PartnershipStatus status;

    private String meToOtherRelationshipDescription; //son if partnerId is my son - codes are from CD-CONTACT-PERSON
    private String otherToMeRelationshipDescription; //father/mother if partnerId is my son

    //  John is patient 1 and is the father of patient 2
    //  patient 1 - partnership(meToOther...=son,otherToMeRelationshipDescription=father) - partnerId - patient 2
    //

    String partnerId; //Person: can either be a patient or a contactPerson


    public @Nullable PartnershipType getType() {
        return type;
    }

    public void setType(PartnershipType type) {
        this.type = type;
    }

    public @Nullable
	PartnershipStatus getStatus() {
        return status;
    }

    public void setStatus(PartnershipStatus status) {
        this.status = status;
    }

    public @Nullable String getMeToOtherRelationshipDescription() {
        return meToOtherRelationshipDescription;
    }

    public void setMeToOtherRelationshipDescription(String meToOtherRelationshipDescription) {
        this.meToOtherRelationshipDescription = meToOtherRelationshipDescription;
    }

    public @Nullable String getOtherToMeRelationshipDescription() {
        return otherToMeRelationshipDescription;
    }

    public void setOtherToMeRelationshipDescription(String otherToMeRelationshipDescription) {
        this.otherToMeRelationshipDescription = otherToMeRelationshipDescription;
    }

    public @Nullable String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

	@JsonIgnore
	public @Nullable String getPartnershipDescription() {
		return partnershipDescription;
	}
	@JsonIgnore
	public void setPartnershipDescription(String partnershipDescription) {
		this.partnershipDescription = partnershipDescription;
	}
}

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

package org.taktik.icure.services.external.rest.v1.dto.embed;

public class PartnershipDto {
	private String partnershipDescription;
    private PartnershipTypeDto type;
    private PartnershipStatusDto status;

    private String meToOtherRelationshipDescription; //son if partnerId is my son
    private String otherToMeRelationshipDescription; //father/mother if partnerId is my son

    String partnerId; //Person: can either be a patient or a contactPerson

    public PartnershipTypeDto getType() {
        return type;
    }

    public void setType(PartnershipTypeDto type) {
        this.type = type;
    }

    public PartnershipStatusDto getStatus() {
        return status;
    }

    public void setStatus(PartnershipStatusDto status) {
        this.status = status;
    }

    public String getMeToOtherRelationshipDescription() {
        return meToOtherRelationshipDescription;
    }

    public void setMeToOtherRelationshipDescription(String meToOtherRelationshipDescription) {
        this.meToOtherRelationshipDescription = meToOtherRelationshipDescription;
    }

    public String getOtherToMeRelationshipDescription() {
        return otherToMeRelationshipDescription;
    }

    public void setOtherToMeRelationshipDescription(String otherToMeRelationshipDescription) {
        this.otherToMeRelationshipDescription = otherToMeRelationshipDescription;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

	public String getPartnershipDescription() {
		return partnershipDescription;
	}

	public void setPartnershipDescription(String partnershipDescription) {
		this.partnershipDescription = partnershipDescription;
	}
}

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

package org.taktik.icure.services.external.rest.v1.dto;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoicingCodeDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.SubContactDto;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDto extends IcureDto {
    protected String groupId; // Several contacts can be combined in a logical contact if they share the same groupId

    protected Long openingDate;
    protected Long closingDate;

    protected String descr;
    protected String location;
    protected String healthcarePartyId;
	protected String externalId;
    protected CodeDto encounterType;

	protected Set<SubContactDto> subContacts = new HashSet<>();
	protected List<ServiceDto> services = new ArrayList<>();

    public ContactDto(){}
    public ContactDto(String healthcarePartyId) {
    	this.healthcarePartyId = healthcarePartyId;
    	responsible=healthcarePartyId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<ServiceDto> getServices() {
        return services;
    }

    public void setServices(List<ServiceDto> services) {
        this.services = services;
    }

    public Long getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Long openingDate) {
        this.openingDate = openingDate;
    }

    public Long getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Long closingDate) {
        this.closingDate = closingDate;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHealthcarePartyId() {
        return healthcarePartyId;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    public CodeDto getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(CodeDto encounterType) {
        this.encounterType = encounterType;
    }

    public Set<SubContactDto> getSubContacts() {
        return subContacts;
    }

    public void setSubContacts(Set<SubContactDto> subContacts) {
        this.subContacts = subContacts;
    }

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	private String encryptedSelf;

	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

}

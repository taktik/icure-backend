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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.entities.embed.SubContact;
import org.taktik.icure.entities.utils.MergeUtil;
import org.taktik.icure.utils.FuzzyValues;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact extends StoredICureDocument {
	@NotNull(autoFix = AutoFix.UUID)
	protected String groupId; // Several contacts can be combined in a logical contact if they share the same groupId

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    protected Long openingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
    protected Long closingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

    protected String descr;
    protected String location;

    //Redundant... Should be responsible
    protected String healthcarePartyId;

	protected String externalId;

    protected Code encounterType;

	@Valid
	protected Set<SubContact> subContacts = new HashSet<>();

    @Valid
    protected Set<Service> services = new TreeSet<>();

	public Contact solveConflictWith(Contact other) {
		super.solveConflictsWith(other);
		this.encryptedSelf = this.encryptedSelf == null ? other.encryptedSelf : this.encryptedSelf;

		this.openingDate = other.openingDate==null?this.openingDate:this.openingDate==null?other.openingDate:Long.valueOf(Math.min(this.openingDate,other.openingDate));
		this.closingDate = other.closingDate==null?this.closingDate:this.closingDate==null?other.closingDate:Long.valueOf(Math.max(this.closingDate,other.closingDate));

		this.descr = this.descr == null ? other.descr : this.descr;
		this.location = this.location == null ? other.location : this.location;
		this.encounterType = this.encounterType == null ? other.encounterType : this.encounterType;

		this.subContacts = MergeUtil.mergeSets(this.subContacts, other.subContacts, new HashSet<>(),
			(a,b)-> (a==null&&b==null)||(a!=null&&b!=null&&Objects.equals(a.getId(),b.getId())),
			(a,b)-> {a.solveConflictWith(b); return a;});

		this.services = MergeUtil.mergeSets(this.services, other.services, new TreeSet<>(),
			(a,b)-> (a==null&&b==null)||(a!=null&&b!=null&&Objects.equals(a.getId(),b.getId())),
			Service::solveConflictWith);

		return this;
	}

	public Contact(){}
    public Contact (String healthcarePartyId) {
    	this.healthcarePartyId = healthcarePartyId;
    	openingDate = FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS);
    	responsible = healthcarePartyId;
    }

    public Set<Service> getServices() {
	    return services;
    }

    public void setServices(Set<Service> services) {
        this.services = new TreeSet<>(services);
    }

    public @Nullable
    Long getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Long openingDate) {
        this.openingDate = openingDate;
    }

    public @Nullable Long getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Long closingDate) {
        this.closingDate = closingDate;
    }

    public @Nullable String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public @Nullable String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public @Nullable String getHealthcarePartyId() {
        return healthcarePartyId;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    public @Nullable Code getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(Code encounterType) {
        this.encounterType = encounterType;
    }

    public Set<SubContact> getSubContacts() {
        return subContacts;
    }

    public void setSubContacts(Set<SubContact> subContacts) {
        this.subContacts = subContacts;
    }

    public @Nullable String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

	public @Nullable String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	private String encryptedSelf;
	@Override
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	@Override
	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}
}

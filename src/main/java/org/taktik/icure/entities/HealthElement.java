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
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.PlanOfAction;
import org.taktik.icure.entities.utils.MergeUtil;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthElement extends StoredICureDocument {
    @NotNull
    private
    String healthElementId; //The Unique UUID common to a group of HealthElements that forms an history

    //Usually one of the following is used (either valueDate or openingDate and closingDate)
    @NotNull(autoFix = AutoFix.FUZZYNOW)
    protected Long valueDate;   // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    protected Long openingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
    protected Long closingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

    protected String descr;
    protected String note;

    protected boolean relevant = true;

    private String idOpeningContact;
    private String idClosingContact;

	private String idService; //When a service is used to create the healthElement

	protected Integer status; //bit 0: active/inactive, bit 1: relevant/irrelevant, bit2 : present/absent, ex: 0 = active,relevant and present

	@Valid
	private List<PlanOfAction> plansOfAction = new java.util.ArrayList<>();

	public HealthElement solveConflictWith(HealthElement other) {
		super.solveConflictsWith(other);

		this.openingDate = other.openingDate==null?this.openingDate:this.openingDate==null?other.openingDate:Long.valueOf(Math.min(this.openingDate,other.openingDate));
		this.closingDate = other.closingDate==null?this.closingDate:this.closingDate==null?other.closingDate:Long.valueOf(Math.max(this.closingDate,other.closingDate));
		this.valueDate = other.valueDate==null?this.valueDate:this.valueDate==null?other.valueDate:Long.valueOf(Math.min(this.valueDate,other.valueDate));

		this.descr = this.descr == null ? other.descr : this.descr;
		this.note = this.note == null ? other.note : this.note;

		this.idOpeningContact = this.idOpeningContact == null ? other.idOpeningContact : this.idOpeningContact;
		this.idClosingContact = this.idClosingContact == null ? other.idClosingContact : this.idClosingContact;
		this.idService = this.idService == null ? other.idService : this.idService;

		this.status = this.status == null ? other.status : this.status;

		this.plansOfAction = MergeUtil.mergeListsDistinct(this.plansOfAction, other.plansOfAction,
			(a,b)-> (a==null&&b==null)||(a!=null&&b!=null&&Objects.equals(a.getId(),b.getId())),
			PlanOfAction::solveConflictWith);

		return this;
	}

	public Long getValueDate() {
        return valueDate;
    }

    public void setValueDate(Long valueDate) {
        this.valueDate = valueDate;
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

    public boolean isRelevant() {
        return relevant;
    }

    public void setRelevant(boolean relevant) {
        this.relevant = relevant;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public String getIdOpeningContact() {
        return idOpeningContact;
    }

    public void setIdOpeningContact(String idOpeningContact) {
        this.idOpeningContact = idOpeningContact;
    }

    public String getIdClosingContact() {
        return idClosingContact;
    }

    public void setIdClosingContact(String idClosingContact) {
        this.idClosingContact = idClosingContact;
    }

    public List<PlanOfAction> getPlansOfAction() {
    	if(plansOfAction == null) plansOfAction = new ArrayList<>();
        return plansOfAction;
    }

    public void setPlansOfAction(List<PlanOfAction> plansOfAction) {
        this.plansOfAction = plansOfAction;
    }

    public String getHealthElementId() {
        return healthElementId;
    }

    public void setHealthElementId(String healthElementId) {
        this.healthElementId = healthElementId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public String getIdService() {
		return idService;
	}

	public void setIdService(String idService) {
		this.idService = idService;
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

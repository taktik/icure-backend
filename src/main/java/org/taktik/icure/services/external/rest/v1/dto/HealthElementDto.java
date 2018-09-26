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

import org.taktik.icure.services.external.rest.v1.dto.embed.PlanOfActionDto;
import org.taktik.icure.validation.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class HealthElementDto extends IcureDto {
	private static final long serialVersionUID = 1L;

    String healthElementId; //The Unique UUID common to a group of HealthElements that form an history

    private String descr;
    private String note;

    protected boolean relevant = true;

    protected Long valueDate;

    protected Long openingDate;
    protected Long closingDate;

    protected String idOpeningContact;
    protected String idClosingContact;

    protected Integer status; //bit 0: active/inactive, bit 1: relevant/irrelevant, bit2 : present/absent, ex: 0 = active,relevant and present

	protected String idService; //When a service is used to create the healthElement

	protected List<PlanOfActionDto> plansOfAction;

	public HealthElementDto() {
	}

	public HealthElementDto(String healthElementId, String descr, List<PlanOfActionDto> plansOfAction, CodeDto... tags) {
        this.healthElementId = healthElementId;
        this.descr = descr;
        this.plansOfAction = plansOfAction;
		if (tags != null && tags.length != 0) {
			this.tags = new HashSet<>(Arrays.asList(tags));
		} else {
			this.tags = new HashSet<>();
		}

	}

    public String getHealthElementId() {
        return healthElementId;
    }

    public void setHealthElementId(String healthElementId) {
        this.healthElementId = healthElementId;
    }

    public List<PlanOfActionDto> getPlansOfAction() {
		if(plansOfAction==null ) plansOfAction= new ArrayList<PlanOfActionDto>();
		return plansOfAction;
	}

	public void setPlansOfAction(List<PlanOfActionDto> plansOfAction) {
		this.plansOfAction = plansOfAction;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public void setOpeningDate(Long openingDate) {
        this.openingDate = openingDate;
    }

    public Long getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Long closingDate) {
        this.closingDate = closingDate;
    }

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

    public boolean isRelevant() {
        return relevant;
    }

    public void setRelevant(boolean relevant) {
        this.relevant = relevant;
    }

    public Long getOpeningDate() {
        return openingDate;
    }

    public Long getValueDate() {
        return valueDate;
    }

    public void setValueDate(Long valueDate) {
        this.valueDate = valueDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

	public String getIdService() {
		return idService;
	}

	public void setIdService(String idService) {
		this.idService = idService;
	}
}

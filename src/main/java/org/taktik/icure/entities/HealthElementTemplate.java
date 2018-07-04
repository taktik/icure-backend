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
import org.taktik.icure.entities.embed.PlanOfActionTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthElementTemplate extends StoredICureDocument {
    protected String descr;

    protected boolean relevant = true;
    protected Integer status; //bit 0: active/inactive, bit 1: relevant/irrelevant, bit2 : present/absent, ex: 0 = active,relevant and present
    @Valid
    protected List<PlanOfActionTemplate> plansOfAction = new ArrayList<>();

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public List<PlanOfActionTemplate> getPlansOfAction() {
		return plansOfAction;
	}

	public void setPlansOfAction(List<PlanOfActionTemplate> plansOfAction) {
		this.plansOfAction = plansOfAction;
	}

	public boolean isRelevant() {
		return relevant;
	}

	public void setRelevant(boolean relevant) {
		this.relevant = relevant;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

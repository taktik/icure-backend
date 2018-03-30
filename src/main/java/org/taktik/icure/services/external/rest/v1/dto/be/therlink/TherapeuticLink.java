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

package org.taktik.icure.services.external.rest.v1.dto.be.therlink;

import java.io.Serializable;

import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.KmehrPatient;

public class TherapeuticLink implements Serializable {
	protected KmehrPatient patient;
	protected TherapeuticLinkHcParty hcParty;
	protected String type;
	protected Long startDate;
	protected Long endDate;
	protected String comment;
	protected String status;

	public TherapeuticLink(KmehrPatient patient, TherapeuticLinkHcParty hcParty, String type) {
		this.patient = patient;
		this.hcParty = hcParty;
		this.type = type;
	}

	public TherapeuticLink() {
	}

	public KmehrPatient getPatient() {
		return patient;
	}

	public void setPatient(KmehrPatient patient) {
		this.patient = patient;
	}

	public TherapeuticLinkHcParty getHcParty() {
		return hcParty;
	}

	public void setHcParty(TherapeuticLinkHcParty hcParty) {
		this.hcParty = hcParty;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

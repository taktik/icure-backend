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

package org.taktik.icure.services.external.rest.v1.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NotificationRuleDto {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected String id;
	protected List<PlannedSendingDto> plannedSendings;
	protected String patientId;
	protected String healthcarePartyId;
	protected boolean enabled=true;
	protected Date validFrom;
	protected Date validUntil;
	protected List<NotificationEventDto> events;
	protected String generalNoticationRuleId;
	protected String descr;
	protected String content;
	protected TypeDto type;

	public enum StatusDto {
		   SENT, RECEIVED, ERROR
	   }

	 public enum TypeDto implements Serializable {
	    	sms,email
	    }

	public List<PlannedSendingDto> getPlannedSendings() {
		if(plannedSendings==null) plannedSendings = new ArrayList<PlannedSendingDto>();
		return plannedSendings;
	}

	public void setPlannedSendings(List<PlannedSendingDto> plannedSending) {
		this.plannedSendings = plannedSending;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getHealthcarePartyId() {
		return healthcarePartyId;
	}

	public void setHealthcarePartyId(String healthcarePartyId) {
		this.healthcarePartyId = healthcarePartyId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public TypeDto getType() {
		return type;
	}

	public void setType(TypeDto type) {
		this.type = type;
	}

	public String getGeneralNoticationRuleId() {
		return generalNoticationRuleId;
	}

	public void setGeneralNoticationRuleId(String generalNoticationRuleId) {
		this.generalNoticationRuleId = generalNoticationRuleId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<NotificationEventDto> getEvents() {
		return events;
	}

	public void setEvents(List<NotificationEventDto> events) {
		this.events = events;
	}

}

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

import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentDto;
import org.taktik.icure.utils.FuzzyValues;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class AppointmentDto implements Serializable {
	private String zoneId;
	private String patientId;
	private String userId;

	private String prescriptorComment;
	private String patientComment;
	private String comment;

	private String type;
	private String location;

	private String status;

	private Boolean paid;
	private Double amount;

	private Long startTime;
	private Long endTime;

	public AppointmentDto() {
	}

	public AppointmentDto(MikronoAppointmentDto a) {
		this.patientId = a.getCustomerRef();
		this.userId = a.getOwnerRef();
		this.prescriptorComment = a.getPrescriptorComments();
		this.patientComment = a.getCustomerComments();
		this.comment = a.getComments();

		this.type = a.getType();
		this.location = a.getLocationText();

		this.status = a.getStatus();

		this.paid = a.getPaid();
		this.amount = a.getPrice();

		ZoneId zoneId = a.getTimezoneId() != null ? ZoneId.of(a.getTimezoneId()) : ZoneId.systemDefault();

		this.startTime = FuzzyValues.getFuzzyDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(a.getStartTime()), zoneId), ChronoUnit.SECONDS);
		this.endTime = FuzzyValues.getFuzzyDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(a.getEndTime()), zoneId), ChronoUnit.SECONDS);

		this.zoneId = a.getTimezoneId();
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPrescriptorComment() {
		return prescriptorComment;
	}

	public void setPrescriptorComment(String prescriptorComment) {
		this.prescriptorComment = prescriptorComment;
	}

	public String getPatientComment() {
		return patientComment;
	}

	public void setPatientComment(String patientComment) {
		this.patientComment = patientComment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
}

/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.dto.be.mikrono;

public class MikronoAppointmentDto {
	private String appointmentTypeId;
	private String comments;
	private String customerComments;
	private String customerRef;
	private Long endTime;
	private Boolean onWaitingList;
	private String ownerRef;
	private String color;
	private String prescriptorComments;
	private Long startTime;
	private Double price;
	private Boolean paid;
	private String reference;
	private Boolean firstAppointment;
	private String status;
	private String type;
	private String locationText;
	private String timezoneId;

	public String getAppointmentTypeId() {
		return appointmentTypeId;
	}

	public void setAppointmentTypeId(String appointmentTypeId) {
		this.appointmentTypeId = appointmentTypeId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCustomerComments() {
		return customerComments;
	}

	public void setCustomerComments(String customerComments) {
		this.customerComments = customerComments;
	}

	public String getCustomerRef() {
		return customerRef;
	}

	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Boolean getOnWaitingList() {
		return onWaitingList;
	}

	public void setOnWaitingList(Boolean onWaitingList) {
		this.onWaitingList = onWaitingList;
	}

	public String getOwnerRef() {
		return ownerRef;
	}

	public void setOwnerRef(String ownerRef) {
		this.ownerRef = ownerRef;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getPrescriptorComments() {
		return prescriptorComments;
	}

	public void setPrescriptorComments(String prescriptorComments) {
		this.prescriptorComments = prescriptorComments;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Boolean getFirstAppointment() {
		return firstAppointment;
	}

	public void setFirstAppointment(Boolean firstAppointment) {
		this.firstAppointment = firstAppointment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocationText() {
		return locationText;
	}

	public void setLocationText(String locationText) {
		this.locationText = locationText;
	}

	public String getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}
}

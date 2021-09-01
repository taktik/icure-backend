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

import java.io.Serializable;
import java.util.Date;

public class AppointmentImportDto implements Serializable{
	private static final long serialVersionUID = 8923289590887455347L;

	private String comments = null;

	private String externalCustomerId = null;

	private String customerId = null;

	private String customerComments = null;

	private String title = null;

	private Date endTime = null;

	private Date startTime = null;

	private String type;

    private String appointmentTypeId;

    private String ownerRef;

	private String customerName;

	private String customerFirstname;

	private String customerEmail;


	private String city;
	private String postcode;
	private String street;
	private String sex;

    private String externalId;


	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getPostcode() {
		return postcode;
	}



	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}



	public String getStreet() {
		return street;
	}



	public void setStreet(String street) {
		this.street = street;
	}



	public String getSex() {
		return sex;
	}



	public void setSex(String sex) {
		this.sex = sex;
	}



	public String getCustomerEmail() {
		return customerEmail;
	}



	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}



	private Date customerBirthDate;

	private String customerGsm;

	private String customerFixPhone;

	public AppointmentImportDto() {};



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

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	public String getExternalCustomerId() {
		return externalCustomerId;
	}



	public void setExternalCustomerId(String externalCustomerId) {
		this.externalCustomerId = externalCustomerId;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getCustomerId() {
		return customerId;
	}



	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}



	public String getCustomerName() {
		return customerName;
	}



	public String getCustomerFirstname() {
		return customerFirstname;
	}



	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}



	public void setCustomerFirstname(String customerFirstname) {
		this.customerFirstname = customerFirstname;
	}



	public Date getCustomerBirthDate() {
		return customerBirthDate;
	}



	public String getCustomerGsm() {
		return customerGsm;
	}



	public String getCustomerFixPhone() {
		return customerFixPhone;
	}



	public void setCustomerBirthDate(Date customerBirthDate) {
		this.customerBirthDate = customerBirthDate;
	}



	public void setCustomerGsm(String customerGsm) {
		this.customerGsm = customerGsm;
	}



	public void setCustomerFixPhone(String customerFixPhone) {
		this.customerFixPhone = customerFixPhone;
	}

    public String getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(String appointmentTypeId) {
        this.appointmentTypeId = appointmentTypeId;
    }

    public String getOwnerRef() {
        return ownerRef;
    }

    public void setOwnerRef(String ownerRef) {
        this.ownerRef = ownerRef;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}

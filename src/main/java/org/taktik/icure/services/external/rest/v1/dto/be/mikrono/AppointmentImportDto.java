package org.taktik.icure.services.external.rest.v1.dto.be.mikrono;

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

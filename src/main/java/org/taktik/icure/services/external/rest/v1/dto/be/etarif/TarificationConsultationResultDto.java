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

package org.taktik.icure.services.external.rest.v1.dto.be.etarif;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TarificationConsultationResultDto {
	private Date birthdate;
	private String CT1;
	private String CT2;
	private Date date;
	private Date deceased;
	private List<String> codes = new ArrayList<>();
	private List<TarifConsultationErrorDto> errors = new ArrayList<>();
	private List<TarifConsultationPaymentDto> fees = new ArrayList<>();
	private List<String> financialContracts = new ArrayList<>();
	private String firstName;
	private Date insurancePeriodEnd;
	private Date insurancePeriodStart;
	private int justification;
	private String lastName;
	private String niss;
	private List<TarifConsultationPaymentDto> patientFees = new ArrayList<>();
	private String sex;
	private List<TarifConsultationPaymentDto> reimbursements = new ArrayList<>();


	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public String getCT1() {
		return CT1;
	}

	public void setCT1(String CT1) {
		this.CT1 = CT1;
	}

	public String getCT2() {
		return CT2;
	}

	public void setCT2(String CT2) {
		this.CT2 = CT2;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDeceased() {
		return deceased;
	}

	public void setDeceased(Date deceased) {
		this.deceased = deceased;
	}

	public List<TarifConsultationErrorDto> getErrors() {
		return errors;
	}

	public void setErrors(List<TarifConsultationErrorDto> errors) {
		this.errors = errors;
	}

	public List<TarifConsultationPaymentDto> getFees() {
		return fees;
	}

	public void setFees(List<TarifConsultationPaymentDto> fees) {
		this.fees = fees;
	}

	public List<String> getFinancialContracts() {
		return financialContracts;
	}

	public void setFinancialContracts(List<String> financialContracts) {
		this.financialContracts = financialContracts;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Date getInsurancePeriodEnd() {
		return insurancePeriodEnd;
	}

	public void setInsurancePeriodEnd(Date insurancePeriodEnd) {
		this.insurancePeriodEnd = insurancePeriodEnd;
	}

	public Date getInsurancePeriodStart() {
		return insurancePeriodStart;
	}

	public void setInsurancePeriodStart(Date insurancePeriodStart) {
		this.insurancePeriodStart = insurancePeriodStart;
	}

	public int getJustification() {
		return justification;
	}

	public void setJustification(int justification) {
		this.justification = justification;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNiss() {
		return niss;
	}

	public void setNiss(String niss) {
		this.niss = niss;
	}

	public List<TarifConsultationPaymentDto> getPatientFees() {
		return patientFees;
	}

	public void setPatientFees(List<TarifConsultationPaymentDto> patientFees) {
		this.patientFees = patientFees;
	}

	public List<TarifConsultationPaymentDto> getReimbursements() {
		return reimbursements;
	}

	public void setReimbursements(List<TarifConsultationPaymentDto> reimbursements) {
		this.reimbursements = reimbursements;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public static class TarifConsultationErrorDto implements Serializable {
		private String code;
		private String description;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class TarifConsultationPaymentDto implements Serializable {
		private double amount;
		private String currencyUnit;
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}// TODO: double
		public String getCurrencyUnit() {
			return currencyUnit;
		}
		public void setCurrencyUnit(String currencyUnit) {
			this.currencyUnit = currencyUnit;
		}
	}

}

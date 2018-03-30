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

package org.taktik.icure.be.ehealth.dto.etarif;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.CostType;
import be.fgov.ehealth.standards.kmehr.schema.v1.DateType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import be.fgov.ehealth.standards.kmehr.schema.v1.SexType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import be.fgov.ehealth.standards.kmehr.schema.v1.UnitType;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.taktik.icure.be.ehealth.dto.Error;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 30/04/15
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class TarificationConsultationResult implements Serializable {
	private Date birthdate;
	private String CT1;
	private String CT2;
	private Date date;
	private Date deceased;
	private List<String> codes = new ArrayList<>();
	private List<Error> errors = new ArrayList<>();
	private List<Payment> fees = new ArrayList<>();
	private List<String> financialContracts = new ArrayList<String>();
	private String firstName;
	private Date insurancePeriodEnd;
	private Date insurancePeriodStart;
	private int justification;
	private String lastName;
	private String niss;
	private List<Payment> patientFees = new ArrayList<Payment>();
	private Sex sex;
	private List<Payment> reimbursements = new ArrayList<Payment>();

	public void fill(PersonType patient) {
		this.setLastName(patient.getFamilyname());
		this.setFirstName(patient.getFirstnames());
		this.setBirthdate(asDate(patient.getBirthdate()));
		this.setDeceased(asDate(patient.getDeathdate()));
		this.setSex(asSex(patient.getSex()));
		this.setInsurancePeriodStart(asDate(patient.getInsurancystatus().getBegindate()));
		this.setInsurancePeriodEnd(asDate(patient.getInsurancystatus().getEnddate()));
		this.setCT1(patient.getInsurancystatus().getCg1());
		this.setCT2(patient.getInsurancystatus().getCg2());
		this.setDate(asDate(patient.getRecorddatetime()));
	}

	public void fill(List<TransactionType> transactions) {
		for (TransactionType transaction : transactions) {
			List<ItemType> items = transaction.getItem();

			for (ItemType item : items) {
				List<CDITEM> cds = item.getCds();

				for (CDITEM cd : cds) {
					CostType cost = item.getCost();
					// Fee
					if (cd.getValue().equals("fee") && cost != null) {
						fees.add(getPayment(cost));
					}
					// Reimbursements
					if (cd.getValue().equals("reimbursement") && cost != null) {
						reimbursements.add(getPayment(cost));
					}
					// Patient fees
					if (cd.getValue().equals("patientfee") && cost != null) {
						patientFees.add(getPayment(cost));
					}
					// Financial contracts
					if (cd.getValue().equals("financialcontract")) {
						for (ContentType content : item.getContents()) {
							for (IDKMEHR id : content.getIds()) {
								financialContracts.add(id.getValue());
							}
						}
					}
					// Codes
					if (cd.getValue().equals("claim")) {
						for (ContentType content : item.getContents()) {
							for (CDCONTENT cdc : content.getCds()) {
								if (cdc.getS().equals(CDCONTENTschemes.CD_NIHDI)) { codes.add(cdc.getValue()); }
							}
						}
					}
					// Justification
					if (cd.getValue().equals("justification") && item.getContents().size() > 0) {
						List<CDCONTENT> contentsCds = item.getContents().get(0).getCds();
						if (contentsCds != null && contentsCds.size() > 0) {
							if (contentsCds.get(0).getValue().length()>0 && contentsCds.get(0).getValue().matches("[0-9]+")) { justification = Integer.parseInt(contentsCds.get(0).getValue()); }
						}
					}
				}
			}
		}
	}

	private Payment getPayment(CostType cost) {
		Payment fee = new Payment();
		if (cost.getDecimal() != null) {
			fee.setAmount(cost.getDecimal().doubleValue());
		}
		UnitType unit = cost.getUnit();
		if (unit != null && unit.getCd() != null && unit.getCd().getValue() != null) {
			fee.setCurrencyUnit(unit.getCd().getValue());
		}
		return fee;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setFirstName(List<String> firstNames) {
		StringBuilder names = null;
		for (String firstName : firstNames) {
			if (names == null) {
				names = new StringBuilder(firstName);
			} else {
				names.append(" ").append(firstName);
			}
		}
		this.firstName = names == null ? "" : names.toString();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setNiss(String niss) {
		this.niss = niss;
	}

	public String getNiss() {
		return niss;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setDeceased(Date deceased) {
		this.deceased = deceased;
	}

	public Date getDeceased() {
		return deceased;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Sex getSex() {
		return sex;
	}

	public void setInsurancePeriodStart(Date insurancePeriodStart) {
		this.insurancePeriodStart = insurancePeriodStart;
	}

	public Date getInsurancePeriodStart() {
		return insurancePeriodStart;
	}

	public void setInsurancePeriodEnd(Date insurancePeriodEnd) {
		this.insurancePeriodEnd = insurancePeriodEnd;
	}

	public Date getInsurancePeriodEnd() {
		return insurancePeriodEnd;
	}

	public void setCT1(String CT1) {
		this.CT1 = CT1;
	}

	public String getCT1() {
		return CT1;
	}

	public void setCT2(String CT2) {
		this.CT2 = CT2;
	}

	public String getCT2() {
		return CT2;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public List<Payment> getReimbursements() {
		return reimbursements;
	}

	public void setReimbursements(List<Payment> reimbursements) {
		this.reimbursements = reimbursements;
	}

	public List<Payment> getPatientFees() {
		return patientFees;
	}

	public void setPatientFees(List<Payment> patientFees) {
		this.patientFees = patientFees;
	}

	public List<String> getFinancialContracts() {
		return financialContracts;
	}

	public void setFinancialContracts(List<String> financialContracts) {
		this.financialContracts = financialContracts;
	}

	public int getJustification() {
		return justification;
	}

	public void setJustification(int justification) {
		this.justification = justification;
	}

	public List<Payment> getFees() {
		return fees;
	}

	public void setFees(List<Payment> fees) {
		this.fees = fees;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	private Date asDate(DateTime date) {
		if (date != null) {
			return new Date(date.getMillis());
		} else {
			return null;
		}
	}

	private Date asDate(DateType date) {
		if (date != null && date.getDate() != null && date.getDate().getMillis() > 0) {
			return new Date(date.getDate().getMillis());
		} else {
			return null;
		}
	}

	private Sex asSex(SexType sex) {
		if (sex != null && sex.getCd() != null && sex.getCd().getValue() != null) {
			return sex.getCd().getValue().equals(CDSEXvalues.FEMALE) ? Sex.FEMALE : Sex.MALE;
		} else {
			return null;
		}
	}

	public static class Payment implements Serializable {
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


	public enum Sex implements Serializable {
		MALE, FEMALE
	}
}

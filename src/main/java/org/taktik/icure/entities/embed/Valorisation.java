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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Valorisation implements Serializable {
	protected Long startOfValidity; //yyyyMMdd
	protected Long endOfValidity; //yyyyMMdd

	protected String predicate;

	protected Double totalAmount; //=reimbursement+doctorSupplement+intervention
	protected Double reimbursement;
	protected Double patientIntervention;
	protected Double doctorSupplement;
	protected Double vat;

	protected java.util.Map<String, String> label; //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}


	public Long getStartOfValidity() {
		return startOfValidity;
	}

	public void setStartOfValidity(Long startOfValidity) {
		this.startOfValidity = startOfValidity;
	}

	public Long getEndOfValidity() {
		return endOfValidity;
	}

	public void setEndOfValidity(Long endOfValidity) {
		this.endOfValidity = endOfValidity;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getReimbursement() {
		return reimbursement;
	}

	public void setReimbursement(Double reimbursement) {
		this.reimbursement = reimbursement;
	}

	public Double getPatientIntervention() {
		return patientIntervention;
	}

	public void setPatientIntervention(Double patientIntervention) {
		this.patientIntervention = patientIntervention;
	}

	public Double getDoctorSupplement() {
		return doctorSupplement;
	}

	public void setDoctorSupplement(Double doctorSupplement) {
		this.doctorSupplement = doctorSupplement;
	}

	public Map<String, String> getLabel() {
		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Valorisation that = (Valorisation) o;
		return Objects.equals(startOfValidity, that.startOfValidity) &&
				Objects.equals(endOfValidity, that.endOfValidity) &&
				Objects.equals(predicate, that.predicate) &&
				Objects.equals(totalAmount, that.totalAmount) &&
				Objects.equals(reimbursement, that.reimbursement) &&
				Objects.equals(patientIntervention, that.patientIntervention) &&
				Objects.equals(doctorSupplement, that.doctorSupplement) &&
				Objects.equals(vat, that.vat) &&
				Objects.equals(label, that.label);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startOfValidity, endOfValidity, predicate, totalAmount, reimbursement, patientIntervention, doctorSupplement, vat, label);
	}
}

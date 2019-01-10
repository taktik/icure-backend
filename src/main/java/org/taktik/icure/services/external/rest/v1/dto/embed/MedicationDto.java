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

package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.services.external.rest.v1.dto.CodeDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MedicationDto implements Serializable {
	protected String compoundPrescription;
	protected SubstanceproductDto substanceProduct;
	protected MedicinalproductDto medicinalProduct;

	Integer numberOfPackages;

	String batch;

	String instructionForPatient;
	String commentForDelivery;
	String drugRoute; //CD-DRUG-ROUTE
	String temporality; //CD-TEMPORALITY : chronic, acute, oneshot

	DurationDto duration;
	RenewalDto renewal;

	Long beginMoment;
	Long endMoment;

	Boolean knownUsage;

	CodeDto frequency;
	CodeDto reimbursementReason;
	Boolean substitutionAllowed;


	List<RegimenItemDto> regimen;
	String posology;

	Map<String, ContentDto> options;
	Map<String, ParagraphAgreementDto> agreements;

	String medicationSchemeIdOnSafe;
	Integer medicationSchemeSafeVersion;
	Long medicationSchemeTimeStampOnSafe;
	String medicationSchemeDocumentId;
	String safeIdName; //can be: vitalinkuri, RSWID, RSBID
	String idOnSafes; //medicationschemeelement : value of vitalinkuri, RSBID, RSWID
	Long timestampOnSafe; //transaction date+time
	Boolean changeValidated; //accept change on safe
	Boolean newSafeMedication; //new medication on safe
	String medicationUse; //free text
	String beginCondition; //free text
	String endCondition; //free text
	String origin; // regularprocess, recorded
	Boolean medicationChanged;
	Boolean posologyChanged;

	String prescriptionRID;

	public Map<String, ContentDto> getOptions() {
		return options;
	}

	public void setOptions(Map<String, ContentDto> options) {
		this.options = options;
	}

	public String getCompoundPrescription() {
		return compoundPrescription;
	}

	public void setCompoundPrescription(String compoundPrescription) {
		this.compoundPrescription = compoundPrescription;
	}

	public SubstanceproductDto getSubstanceProduct() {
		return substanceProduct;
	}

	public void setSubstanceProduct(SubstanceproductDto substanceProduct) {
		this.substanceProduct = substanceProduct;
	}

	public MedicinalproductDto getMedicinalProduct() {
		return medicinalProduct;
	}

	public void setMedicinalProduct(MedicinalproductDto medicinalProduct) {
		this.medicinalProduct = medicinalProduct;
	}

	public Integer getNumberOfPackages() {
		return numberOfPackages;
	}

	public void setNumberOfPackages(Integer numberOfPackages) {
		this.numberOfPackages = numberOfPackages;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Boolean getKnownUsage() {
		return knownUsage;
	}

	public void setKnownUsage(Boolean knownUsage) {
		this.knownUsage = knownUsage;
	}

	public String getInstructionForPatient() {
		return instructionForPatient;
	}

	public void setInstructionForPatient(String instructionForPatient) {
		this.instructionForPatient = instructionForPatient;
	}

	public String getCommentForDelivery() {
		return commentForDelivery;
	}

	public void setCommentForDelivery(String commentForDelivery) {
		this.commentForDelivery = commentForDelivery;
	}

	public Long getBeginMoment() {
		return beginMoment;
	}

	public void setBeginMoment(Long beginMoment) {
		this.beginMoment = beginMoment;
	}

	public Long getEndMoment() {
		return endMoment;
	}

	public void setEndMoment(Long endMoment) {
		this.endMoment = endMoment;
	}

	public List<RegimenItemDto> getRegimen() {
		return regimen;
	}

	public void setRegimen(List<RegimenItemDto> regimen) {
		this.regimen = regimen;
	}

	public String getPosology() { return posology; }

	public void setPosology(String posology) { this.posology = posology; }

	public DurationDto getDuration() {
		return duration;
	}

	public void setDuration(DurationDto duration) {
		this.duration = duration;
	}

	public Map<String, ParagraphAgreementDto> getAgreements() {
		return agreements;
	}

	public void setAgreements(Map<String, ParagraphAgreementDto> agreements) {
		this.agreements = agreements;
	}

	public CodeDto getFrequency() { return frequency; }

	public void setFrequency(CodeDto frequency) { this.frequency = frequency; }

	public CodeDto getReimbursementReason() { return reimbursementReason; }

	public void setReimbursementReason(CodeDto reimbursementReason) { this.reimbursementReason = reimbursementReason; }

	public Boolean getSubstitutionAllowed() { return substitutionAllowed; }

	public void setSubstitutionAllowed(Boolean substitutionAllowed) { this.substitutionAllowed = substitutionAllowed; }

	public RenewalDto getRenewal() { return renewal; }

	public void setRenewal(RenewalDto renewal) { this.renewal = renewal; }

	public String getDrugRoute() { return drugRoute; }

	public void setDrugRoute(String drugRoute) { this.drugRoute = drugRoute; }

	public String getTemporality() { return temporality; }

	public void setTemporality(String temporality) { this.temporality = temporality; }

	public String getMedicationSchemeIdOnSafe() { return medicationSchemeIdOnSafe; }

	public void setMedicationSchemeIdOnSafe(String medicationSchemeIdOnSafe) { this.medicationSchemeIdOnSafe = medicationSchemeIdOnSafe; }

	public Integer getMedicationSchemeSafeVersion() { return medicationSchemeSafeVersion; }

	public void setMedicationSchemeSafeVersion(Integer medicationSchemeSafeVersion) { this.medicationSchemeSafeVersion = medicationSchemeSafeVersion; }

	public Long getMedicationSchemeTimeStampOnSafe() { return medicationSchemeTimeStampOnSafe; }

	public void setMedicationSchemeTimeStampOnSafe(Long medicationSchemeTimeStampOnSafe) { this.medicationSchemeTimeStampOnSafe = medicationSchemeTimeStampOnSafe; }

	public String getMedicationSchemeDocumentId() { return medicationSchemeDocumentId; }

	public void setMedicationSchemeDocumentId(String medicationSchemeDocumentId) { this.medicationSchemeDocumentId = medicationSchemeDocumentId; }

	public String getSafeIdName() { return safeIdName; }

	public void setSafeIdName(String safeIdName) { this.safeIdName = safeIdName; }

	public String getIdOnSafes() { return idOnSafes; }

	public void setIdOnSafes(String idOnSafes) { this.idOnSafes = idOnSafes; }

	public Long getTimestampOnSafe() { return timestampOnSafe; }

	public void setTimestampOnSafe(Long timestampOnSafe) { this.timestampOnSafe = timestampOnSafe; }

	public Boolean getChangeValidated() { return changeValidated; }

	public void setChangeValidated(Boolean changeValidated) { this.changeValidated = changeValidated; }

	public Boolean getNewSafeMedication() { return newSafeMedication; }

	public void setNewSafeMedication(Boolean newSafeMedication) { this.newSafeMedication = newSafeMedication; }

	public String getMedicationUse() { return medicationUse; }

	public void setMedicationUse(String medicationUse) { this.medicationUse = medicationUse; }

	public String getBeginCondition() { return beginCondition; }

	public void setBeginCondition(String beginCondition) { this.beginCondition = beginCondition; }

	public String getEndCondition() { return endCondition; }

	public void setEndCondition(String endCondition) { this.endCondition = endCondition; }

	public String getOrigin() { return origin; }

	public void setOrigin(String origin) { this.origin = origin; }

	public Boolean getMedicationChanged() { return medicationChanged; }

	public void setMedicationChanged(Boolean medicationChanged) { this.medicationChanged = medicationChanged; }

	public Boolean getPosologyChanged() { return posologyChanged; }

	public void setPosologyChanged(Boolean posologyChanged) { this.posologyChanged = posologyChanged; }

	public String getPrescriptionRID() { return prescriptionRID; }

	public void setPrescriptionRID(String prescriptionRID) { this.prescriptionRID = prescriptionRID; }
}

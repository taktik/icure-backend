package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.entities.embed.AgreementAppendix;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ParagraphAgreementDto implements Serializable {
	private Long timestamp;
	private String paragraph;
	private Boolean isAccepted;
	private Boolean isInTreatment;
	private String careProviderReference;
	private String decisionReference;
	private Long start;
	private Long end;
	private Double quantityValue;
	private String quantityUnit;
	private String ioRequestReference;

	private String responseType;
	private Map<String, String> refusalJustification;
	private String coverageType;
	private Double unitNumber;
	private Double strength ;
	private String strengthUnit ;
	private Double restUnitNumber;
	private Double restStrength;
	private String restStrengthUnit;

	private List<AgreementAppendixDto> agreementAppendices;

	private String documentId;


	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getParagraph() {
		return paragraph;
	}

	public void setParagraph(String paragraph) {
		this.paragraph = paragraph;
	}

	public Boolean getAccepted() {
		return isAccepted;
	}

	public void setAccepted(Boolean accepted) {
		isAccepted = accepted;
	}

	public Boolean getInTreatment() {
		return isInTreatment;
	}

	public void setInTreatment(Boolean inTreatment) {
		isInTreatment = inTreatment;
	}

	public String getCareProviderReference() {
		return careProviderReference;
	}

	public void setCareProviderReference(String careProviderReference) {
		this.careProviderReference = careProviderReference;
	}

	public String getDecisionReference() {
		return decisionReference;
	}

	public void setDecisionReference(String decisionReference) {
		this.decisionReference = decisionReference;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public Double getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(Double quantityValue) {
		this.quantityValue = quantityValue;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

	public String getIoRequestReference() {
		return ioRequestReference;
	}

	public void setIoRequestReference(String ioRequestReference) {
		this.ioRequestReference = ioRequestReference;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public Map<String, String> getRefusalJustification() {
		return refusalJustification;
	}

	public void setRefusalJustification(Map<String, String> refusalJustification) {
		this.refusalJustification = refusalJustification;
	}

	public String getCoverageType() {
		return coverageType;
	}

	public void setCoverageType(String coverageType) {
		this.coverageType = coverageType;
	}

	public Double getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(Double unitNumber) {
		this.unitNumber = unitNumber;
	}

	public Double getStrength() {
		return strength;
	}

	public void setStrength(Double strength) {
		this.strength = strength;
	}

	public String getStrengthUnit() {
		return strengthUnit;
	}

	public void setStrengthUnit(String strengthUnit) {
		this.strengthUnit = strengthUnit;
	}

	public Double getRestUnitNumber() {
		return restUnitNumber;
	}

	public void setRestUnitNumber(Double restUnitNumber) {
		this.restUnitNumber = restUnitNumber;
	}

	public Double getRestStrength() {
		return restStrength;
	}

	public void setRestStrength(Double restStrength) {
		this.restStrength = restStrength;
	}

	public String getRestStrengthUnit() {
		return restStrengthUnit;
	}

	public void setRestStrengthUnit(String restStrengthUnit) {
		this.restStrengthUnit = restStrengthUnit;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}


}

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

package org.taktik.icure.be.ehealth.dto.civics;

import java.util.Date;

public class HVerse {

	String chapterName;
	String paragraphName;
	Long verseSeq;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long verseNum;
	Long verseSeqParent;
	Long verseLevel;
	String verseType;
	String checkBoxInd;
	Long minCheckNum;
	Long andClauseNum;
	String textFr;
	String textNl;
	String requestType;
	Long agreementTerm;
	String agreementTermUnit;
	Long maxPackageNumber;
	String purchasingAdvisorQualList;
	String legalReference;
	Date modificationDate;
	Long agreementYearMax;
	Long agreementRenewalMax;
	String sexRestricted;
	Double minimumAgeAuthorized;
	Double maximumAgeAuthorized;
	Double maximumContentQuantity;
	String maximumContentUnit;
	Double maximumStrengthQuantity;
	String maximumStrengthUnit;
	Double maximumDurationQuantity;
	String maximumDurationUnit;
	String otherAddedDocumentInd;
	String minimumAgeAuthorizedUnit;
	String maximumAgeAuthorizedUnit;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getParagraphName() {
        return paragraphName;
    }

    public void setParagraphName(String paragraphName) {
        this.paragraphName = paragraphName;
    }

    public Long getVerseSeq() {
        return verseSeq;
    }

    public void setVerseSeq(Long verseSeq) {
        this.verseSeq = verseSeq;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCreatedTms() {
        return createdTms;
    }

    public void setCreatedTms(Date createdTms) {
        this.createdTms = createdTms;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getVerseNum() {
        return verseNum;
    }

    public void setVerseNum(Long verseNum) {
        this.verseNum = verseNum;
    }

    public Long getVerseSeqParent() {
        return verseSeqParent;
    }

    public void setVerseSeqParent(Long verseSeqParent) {
        this.verseSeqParent = verseSeqParent;
    }

    public Long getVerseLevel() {
        return verseLevel;
    }

    public void setVerseLevel(Long verseLevel) {
        this.verseLevel = verseLevel;
    }

    public String getVerseType() {
        return verseType;
    }

    public void setVerseType(String verseType) {
        this.verseType = verseType;
    }

    public String getCheckBoxInd() {
        return checkBoxInd;
    }

    public void setCheckBoxInd(String checkBoxInd) {
        this.checkBoxInd = checkBoxInd;
    }

    public Long getMinCheckNum() {
        return minCheckNum;
    }

    public void setMinCheckNum(Long minCheckNum) {
        this.minCheckNum = minCheckNum;
    }

    public Long getAndClauseNum() {
        return andClauseNum;
    }

    public void setAndClauseNum(Long andClauseNum) {
        this.andClauseNum = andClauseNum;
    }

    public String getTextFr() {
        return textFr;
    }

    public void setTextFr(String textFr) {
        this.textFr = textFr;
    }

    public String getTextNl() {
        return textNl;
    }

    public void setTextNl(String textNl) {
        this.textNl = textNl;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Long getAgreementTerm() {
        return agreementTerm;
    }

    public void setAgreementTerm(Long agreementTerm) {
        this.agreementTerm = agreementTerm;
    }

    public String getAgreementTermUnit() {
        return agreementTermUnit;
    }

    public void setAgreementTermUnit(String agreementTermUnit) {
        this.agreementTermUnit = agreementTermUnit;
    }

    public Long getMaxPackageNumber() {
        return maxPackageNumber;
    }

    public void setMaxPackageNumber(Long maxPackageNumber) {
        this.maxPackageNumber = maxPackageNumber;
    }

    public String getPurchasingAdvisorQualList() {
        return purchasingAdvisorQualList;
    }

    public void setPurchasingAdvisorQualList(String purchasingAdvisorQualList) {
        this.purchasingAdvisorQualList = purchasingAdvisorQualList;
    }

    public String getLegalReference() {
        return legalReference;
    }

    public void setLegalReference(String legalReference) {
        this.legalReference = legalReference;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Long getAgreementYearMax() {
        return agreementYearMax;
    }

    public void setAgreementYearMax(Long agreementYearMax) {
        this.agreementYearMax = agreementYearMax;
    }

    public Long getAgreementRenewalMax() {
        return agreementRenewalMax;
    }

    public void setAgreementRenewalMax(Long agreementRenewalMax) {
        this.agreementRenewalMax = agreementRenewalMax;
    }

    public String getSexRestricted() {
        return sexRestricted;
    }

    public void setSexRestricted(String sexRestricted) {
        this.sexRestricted = sexRestricted;
    }

    public Double getMinimumAgeAuthorized() {
        return minimumAgeAuthorized;
    }

    public void setMinimumAgeAuthorized(Double minimumAgeAuthorized) {
        this.minimumAgeAuthorized = minimumAgeAuthorized;
    }

    public Double getMaximumAgeAuthorized() {
        return maximumAgeAuthorized;
    }

    public void setMaximumAgeAuthorized(Double maximumAgeAuthorized) {
        this.maximumAgeAuthorized = maximumAgeAuthorized;
    }

    public Double getMaximumContentQuantity() {
        return maximumContentQuantity;
    }

    public void setMaximumContentQuantity(Double maximumContentQuantity) {
        this.maximumContentQuantity = maximumContentQuantity;
    }

    public String getMaximumContentUnit() {
        return maximumContentUnit;
    }

    public void setMaximumContentUnit(String maximumContentUnit) {
        this.maximumContentUnit = maximumContentUnit;
    }

    public Double getMaximumStrengthQuantity() {
        return maximumStrengthQuantity;
    }

    public void setMaximumStrengthQuantity(Double maximumStrengthQuantity) {
        this.maximumStrengthQuantity = maximumStrengthQuantity;
    }

    public String getMaximumStrengthUnit() {
        return maximumStrengthUnit;
    }

    public void setMaximumStrengthUnit(String maximumStrengthUnit) {
        this.maximumStrengthUnit = maximumStrengthUnit;
    }

    public Double getMaximumDurationQuantity() {
        return maximumDurationQuantity;
    }

    public void setMaximumDurationQuantity(Double maximumDurationQuantity) {
        this.maximumDurationQuantity = maximumDurationQuantity;
    }

    public String getMaximumDurationUnit() {
        return maximumDurationUnit;
    }

    public void setMaximumDurationUnit(String maximumDurationUnit) {
        this.maximumDurationUnit = maximumDurationUnit;
    }

    public String getOtherAddedDocumentInd() {
        return otherAddedDocumentInd;
    }

    public void setOtherAddedDocumentInd(String otherAddedDocumentInd) {
        this.otherAddedDocumentInd = otherAddedDocumentInd;
    }

    public String getMinimumAgeAuthorizedUnit() {
        return minimumAgeAuthorizedUnit;
    }

    public void setMinimumAgeAuthorizedUnit(String minimumAgeAuthorizedUnit) {
        this.minimumAgeAuthorizedUnit = minimumAgeAuthorizedUnit;
    }

    public String getMaximumAgeAuthorizedUnit() {
        return maximumAgeAuthorizedUnit;
    }

    public void setMaximumAgeAuthorizedUnit(String maximumAgeAuthorizedUnit) {
        this.maximumAgeAuthorizedUnit = maximumAgeAuthorizedUnit;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


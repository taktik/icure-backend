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

package org.taktik.icure.be.ehealth.dto.civics;

import java.util.Date;

import org.taktik.icure.be.ehealth.dto.civics.*;
import org.taktik.icure.be.ehealth.dto.civics.AppendixType;

public class AddedDocument {

	org.taktik.icure.be.ehealth.dto.civics.FormType formType;
	org.taktik.icure.be.ehealth.dto.civics.AppendixType appendixType;

	String chapterName;
	String paragraphName;
	Long verseSeq;
	Long documentSeq;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	String mimeType;
	byte[] documentContent;
	String addressUrl;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

/*
	public Paragraph getParagraph() {;
		return Paragraph.findByChapterNameAndParagraphName(chapterName,paragraphName);
	};
*/

    public org.taktik.icure.be.ehealth.dto.civics.FormType getFormType() {
        return formType;
    }

    public void setFormType(org.taktik.icure.be.ehealth.dto.civics.FormType formType) {
        this.formType = formType;
    }

    public org.taktik.icure.be.ehealth.dto.civics.AppendixType getAppendixType() {
        return appendixType;
    }

    public void setAppendixType(AppendixType appendixType) {
        this.appendixType = appendixType;
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

    public Long getDocumentSeq() {
        return documentSeq;
    }

    public void setDocumentSeq(Long documentSeq) {
        this.documentSeq = documentSeq;
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

    public Long getNameId() {
        return nameId;
    }

    public void setNameId(Long nameId) {
        this.nameId = nameId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(byte[] documentContent) {
        this.documentContent = documentContent;
    }

    public String getAddressUrl() {
        return addressUrl;
    }

    public void setAddressUrl(String addressUrl) {
        this.addressUrl = addressUrl;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}


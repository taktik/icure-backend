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

import java.io.Serializable;
import java.util.List;

import org.taktik.icure.services.external.rest.v1.dto.CodeDto;

public class PlanOfActionDto implements Serializable {

	private static final long serialVersionUID = 1L;

    protected String id;

    protected String name;
    protected String descr;

    protected Long valueDate;

    protected Long openingDate;
    protected Long closingDate;

    protected String idOpeningContact;
    protected String idClosingContact;

    protected String author; //userId
    protected String responsible; //healthcarePartyId

    protected Long created;
    protected Long modified;
    protected Long endOfLife;

    protected List<CodeDto> codes = new java.util.ArrayList<CodeDto>();
    protected List<CodeDto> tags = new java.util.ArrayList<CodeDto>();

    protected List<String> documentIds;
    protected String prescriberId; //healthcarePartyId
    protected Integer numberOfCares;
    protected Integer status;

    public PlanOfActionDto() {
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Long getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Long openingDate) {
        this.openingDate = openingDate;
    }

    public Long getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Long closingDate) {
        this.closingDate = closingDate;
    }

    public String getIdOpeningContact() {
        return idOpeningContact;
    }

    public void setIdOpeningContact(String idOpeningContact) {
        this.idOpeningContact = idOpeningContact;
    }

    public String getIdClosingContact() {
        return idClosingContact;
    }

    public void setIdClosingContact(String idClosingContact) {
        this.idClosingContact = idClosingContact;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public List<CodeDto> getCodes() {
        return codes;
    }

    public void setCodes(List<CodeDto> codes) {
        this.codes = codes;
    }

    public List<CodeDto> getTags() {
        return tags;
    }

    public void setTags(List<CodeDto> tags) {
        this.tags = tags;
    }

    public Long getEndOfLife() {
        return endOfLife;
    }

    public void setEndOfLife(Long endOfLife) {
        this.endOfLife = endOfLife;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getValueDate() {
        return valueDate;
    }

    public void setValueDate(Long valueDate) {
        this.valueDate = valueDate;
    }

    public List<String> getDocumentIds() { return documentIds; }

    public void setDocumentIds(List<String> documentIds) { this.documentIds = documentIds; }

    public String getPrescriberId() { return prescriberId;}

    public void setPrescriberId(String prescriberId) { this.prescriberId = prescriberId; }

    public Integer getNumberOfCares() { return numberOfCares; }

    public void setNumberOfCares(Integer numberOfCares) { this.numberOfCares = numberOfCares; }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }

    private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}
}

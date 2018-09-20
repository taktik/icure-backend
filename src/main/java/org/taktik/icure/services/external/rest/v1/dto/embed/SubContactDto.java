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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.taktik.icure.services.external.rest.v1.dto.CodeDto;

public class SubContactDto implements Serializable {
    private static final long serialVersionUID = 1L;

    public final static int STATUS_LABO_RESULT = 1;
    public final static int STATUS_UNREAD = 2;
    public final static int STATUS_ALWAYS_DISPLAY = 4;
    public final static int RESET_TO_DEFAULT_VALUES = 8;
    public final static int STATUS_COMPLETE = 16;
    public final static int STATUS_PROTOCOL_RESULT = 32;

    protected String id;
    protected Long openingDate;

    protected String descr;
    protected String protocol;
    protected Integer status; //To be refactored
    protected boolean hasBeenInitialized;
    protected String formId; // form or subform unique ID. Several subcontacts with the same form ID can coexist as long as they are in different contacts or they relate to a different planOfActionID

    protected String planOfActionId;
    protected String healthElementId;
    protected String classificationId;

    protected Long created;
    protected Long modified;
    protected Long endOfLife;

    protected String author; //userId
    protected String responsible; //healthcarePartyId

    protected java.util.Set<CodeDto> codes = new HashSet<>();
    protected java.util.Set<CodeDto> tags = new HashSet<>();


    protected List<ServiceLink> services = new ArrayList<>();

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isHasBeenInitialized() {
        return hasBeenInitialized;
    }

    public void setHasBeenInitialized(boolean hasBeenInitialized) {
        this.hasBeenInitialized = hasBeenInitialized;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public List<ServiceLink> getServices() {
        return services;
    }

    public void setServices(List<ServiceLink> services) {
        this.services = services;
    }

    public Long getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Long openingDate) {
        this.openingDate = openingDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanOfActionId() {
        return planOfActionId;
    }

    public void setPlanOfActionId(String planOfActionId) {
        this.planOfActionId = planOfActionId;
    }

    public String getHealthElementId() {
        return healthElementId;
    }

    public void setHealthElementId(String healthElementId) {
        this.healthElementId = healthElementId;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getEndOfLife() {
        return endOfLife;
    }

    public void setEndOfLife(Long endOfLife) {
        this.endOfLife = endOfLife;
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

    public Set<CodeDto> getCodes() {
        return codes;
    }

    public void setCodes(Set<CodeDto> codes) {
        this.codes = codes;
    }

    public Set<CodeDto> getTags() {
        return tags;
    }

    public void setTags(Set<CodeDto> tags) {
        this.tags = tags;
    }

	private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

    public String getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(String classificationId) {
        this.classificationId = classificationId;
    }
}

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
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.entities.base.ICureDocument;
import org.taktik.icure.entities.utils.MergeUtil;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;
import org.taktik.icure.validation.ValidCode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by aduchate on 06/07/13, 10:09
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubContact implements ICureDocument, Serializable {
    private static final long serialVersionUID = 1L;

    public final static int STATUS_LABO_RESULT = 1;
    public final static int STATUS_UNREAD = 2;
    public final static int STATUS_ALWAYS_DISPLAY = 4;
    public final static int RESET_TO_DEFAULT_VALUES = 8;
    public final static int STATUS_COMPLETE = 16;
    public final static int STATUS_PROTOCOL_RESULT = 32;
	public final static int STATUS_UPLOADED_FILES = 64;

    @NotNull
    protected String id;

    protected String descr;
    protected String protocol;
    protected Integer status; //To be refactored

    @NotNull(autoFix = AutoFix.NOW)
    protected Long created;

    @NotNull(autoFix = AutoFix.NOW)
    protected Long modified;
    protected Long endOfLife;

    @NotNull(autoFix = AutoFix.CURRENTUSERID)
    protected String author; //userId
    @NotNull(autoFix = AutoFix.CURRENTHCPID)
    protected String responsible; //healthcarePartyId

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    protected java.util.Set<CodeStub> codes = new HashSet<>();
    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    protected java.util.Set<CodeStub> tags = new HashSet<>();

    protected String formId; // form or subform unique ID. Several subcontacts with the same form ID can coexist as long as they are in different contacts or they relate to a different planOfActionID
    protected String planOfActionId;
    protected String healthElementId;
    protected String classificationId;

    protected java.util.List<ServiceLink> services = new java.util.ArrayList<ServiceLink>();

    public SubContact solveConflictWith(SubContact other) {
	    this.created = other.created==null?this.created:this.created==null?other.created:Long.valueOf(Math.min(this.created,other.created));
	    this.modified = other.modified==null?this.modified:this.modified==null?other.modified:Long.valueOf(Math.max(this.modified,other.modified));
	    this.codes.addAll(other.codes);
	    this.tags.addAll(other.tags);

	    this.formId = this.formId == null ? other.formId : this.formId;
	    this.planOfActionId = this.planOfActionId == null ? other.planOfActionId : this.planOfActionId;
	    this.healthElementId = this.healthElementId == null ? other.healthElementId : this.healthElementId;
        this.classificationId = this.classificationId == null ? other.classificationId : this.classificationId;

		this.services = MergeUtil.mergeListsDistinct(this.services, other.services,
			(a,b)-> (a==null&&b==null)||(a!=null&&b!=null&& Objects.equals(a.getServiceId(),b.getServiceId())),
			(a,b)-> a);

	    return this;
    }

    public @Nullable String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public @Nullable String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public @Nullable String getPlanOfActionId() {
        return planOfActionId;
    }

    public void setPlanOfActionId(String planOfActionId) {
        this.planOfActionId = planOfActionId;
    }

    public @Nullable String getHealthElementId() {
        return healthElementId;
    }

    public void setHealthElementId(String healthElementId) {
        this.healthElementId = healthElementId;
    }

    public @Nullable Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public @Nullable String getFormId() {
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

    @Override
    public @Nullable Long getCreated() {
        return created;
    }

    @Override
    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public @Nullable Long getModified() {
        return modified;
    }

    @Override
    public void setModified(Long modified) {
        this.modified = modified;
    }

    @Override
    public @Nullable Long getEndOfLife() {
        return endOfLife;
    }

    @Override
    public void setEndOfLife(Long endOfLife) {
        this.endOfLife = endOfLife;
    }

    @Override
    public @Nullable String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public @Nullable
    String getResponsible() {
        return responsible;
    }

    @Override
    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

	private String encryptedSelf;
	@Override
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	@Override
	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

	@Override
    public Set<CodeStub> getCodes() {
        return codes;
    }

    @Override
    public void setCodes(Set<CodeStub> codes) {
        this.codes = codes;
    }

    @Override
    public Set<CodeStub> getTags() {
        return tags;
    }

    @Override
    public void setTags(Set<CodeStub> tags) {
        this.tags = tags;
    }

    public @Nullable String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(String classificationId) {
        this.classificationId = classificationId;
    }
}

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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.ReportVersion;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.DocumentGroup;
import org.taktik.icure.entities.embed.DocumentType;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentTemplate extends StoredDocument implements Serializable {

	@NotNull(autoFix = AutoFix.NOW)
	protected Long modified;
	@NotNull(autoFix = AutoFix.NOW)
	protected Long created;

	protected ReportVersion version;

	protected String owner;
	protected String guid;
	protected String attachmentId;

	@JsonIgnore
	protected byte[] attachment;
	@JsonIgnore
	private boolean attachmentDirty;

	protected DocumentType documentType;

	protected String mainUti;
	protected DocumentGroup group;

	protected String name;
	protected String descr;

	protected String disabled;

	protected Code specialty;

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public boolean isAttachmentDirty() {
		return attachmentDirty;
	}

	public void setAttachmentDirty(boolean attachmentDirty) {
		this.attachmentDirty = attachmentDirty;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getMainUti() {
		return mainUti;
	}

	public void setMainUti(String mainUti) {
		this.mainUti = mainUti;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public DocumentGroup getGroup() {
		return group;
	}

	public void setGroup(DocumentGroup group) {
		this.group = group;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public Code getSpecialty() {
		return specialty;
	}

	public void setSpecialty(Code specialty) {
		this.specialty = specialty;
	}

	public ReportVersion getVersion() {
		return version;
	}

	public void setVersion(ReportVersion version) {
		this.version = version;
	}
}

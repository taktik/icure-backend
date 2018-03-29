/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto;

import java.io.Serializable;

import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentGroupDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentType;

public class DocumentTemplateDto extends StoredDto implements Serializable {

	protected Long modified;
	protected Long created;

	protected String owner;

	protected String guid;

	protected String attachmentId;
	protected DocumentType documentType;

	protected String mainUti;
	protected DocumentGroupDto group;

	protected String name;
	protected String descr;

	protected String disabled;

	protected CodeDto specialty;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
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

	public DocumentGroupDto getGroup() {
		return group;
	}

	public void setGroup(DocumentGroupDto group) {
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

	public CodeDto getSpecialty() {
		return specialty;
	}

	public void setSpecialty(CodeDto specialty) {
		this.specialty = specialty;
	}
}

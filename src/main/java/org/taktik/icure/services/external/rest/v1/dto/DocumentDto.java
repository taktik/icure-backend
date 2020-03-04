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

package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentLocation;
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentStatus;
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentType;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class DocumentDto extends IcureDto {
    protected byte[] attachment;
	protected String attachmentId;
	protected DocumentLocation documentLocation;
	protected DocumentType documentType;
	protected DocumentStatus documentStatus;
	protected String mainUti;
	protected String name;
	protected Set<String> otherUtis;

	protected String externalUri;

	protected String storedICureDocumentId;


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

    public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMainUti() {
		return mainUti;
	}

	public void setMainUti(String mainUti) {
		this.mainUti = mainUti;
	}

	public Set<String> getOtherUtis() {
		return otherUtis;
	}

	public void setOtherUtis(Set<String> otherUtis) {
		this.otherUtis = otherUtis;
	}

	public String getExternalUri() {
		return externalUri;
	}

	public void setExternalUri(String externalUri) {
		this.externalUri = externalUri;
	}

	public String getStoredICureDocumentId() {
		return storedICureDocumentId;
	}

	public void setStoredICureDocumentId(String storedICureDocumentId) {
		this.storedICureDocumentId = storedICureDocumentId;
	}

	public DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	public DocumentLocation getDocumentLocation(){
		return documentLocation;
	}

	public void setDocumentLocation(DocumentLocation documentLocation){
		this.documentLocation = documentLocation;
	}

}

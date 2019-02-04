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
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.DocumentLocation;
import org.taktik.icure.entities.embed.DocumentStatus;
import org.taktik.icure.entities.embed.DocumentType;
import org.taktik.icure.security.CryptoUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document extends StoredICureDocument implements Serializable {
	protected String attachmentId;

    @JsonIgnore
    protected byte[] attachment;
    @JsonIgnore
    private boolean attachmentDirty;

    protected DocumentLocation documentLocation;
    protected DocumentType documentType;
	protected DocumentStatus documentStatus;

    protected String externalUri;

	protected String mainUti;
	protected String name;
    protected Set<String> otherUtis = new HashSet<>();

    //The ICureDocument (Form, Contact, ...) that has been used to generate the document
    protected String storedICureDocumentId;

	public Document solveConflictWith(Document other) {
		super.solveConflictsWith(other);
		this.otherUtis.addAll(other.otherUtis);

		return this;
	}

	public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    @JsonIgnore
    public boolean isAttachmentDirty() {
        return attachmentDirty;
    }

    @JsonIgnore
    public void setAttachmentDirty(boolean attachmentDirty) {
        this.attachmentDirty = attachmentDirty;
    }

    @JsonIgnore
    public byte[] getAttachment() {
        return attachment;
    }

	public byte[] decryptAttachment(List<String> enckeys) {
		if (enckeys != null && enckeys.size()>0) {
			for (String sfk : enckeys) {
				ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
				UUID uuid = UUID.fromString(sfk);
				bb.putLong(uuid.getMostSignificantBits());
				bb.putLong(uuid.getLeastSignificantBits());
				try {
					return CryptoUtils.decryptAES(attachment, bb.array());
				} catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalArgumentException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException ignored) {
				}
			}
		}

		return attachment;
	}

	@JsonIgnore
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

    public String getStoredICureDocumentId() {
        return storedICureDocumentId;
    }

    public void setStoredICureDocumentId(String storedICureDocumentId) {
        this.storedICureDocumentId = storedICureDocumentId;
    }

    public String getExternalUri() {
        return externalUri;
    }

    public void setExternalUri(String externalUri) {
        this.externalUri = externalUri;
    }

	public DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
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

    public DocumentLocation getDocumentLocation() {
        return documentLocation;
    }

    public void setDocumentLocation(DocumentLocation documentLocation) {
        this.documentLocation = documentLocation;
    }

}

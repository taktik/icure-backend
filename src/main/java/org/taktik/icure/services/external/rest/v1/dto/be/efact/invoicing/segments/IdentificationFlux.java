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

package org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments;

@SuppressWarnings("unused")
public class IdentificationFlux {
	private int messageFormatVersion;
	private int messageName;
    private String messageDescription;
    private long messageReference;
	private long messageReferenceOA;
	private int messageStatus;
	private int messageType;
	private String reserve;
	public IdentificationFlux() {
	}
	public IdentificationFlux(int messageFormatVersion, int messageName, long messageReference,
	                          long messageReferenceOA, int messageStatus, int messageType, String reserve) {
		this.messageFormatVersion = messageFormatVersion;
		this.messageName = messageName;
		this.messageReference = messageReference;
		this.messageReferenceOA = messageReferenceOA;
		this.messageStatus = messageStatus;
		this.messageType = messageType;
		this.reserve = reserve;
	}
	public int getMessageFormatVersion() {
		return messageFormatVersion;
	}
	public void setMessageFormatVersion(int messageFormatVersion) {
		this.messageFormatVersion = messageFormatVersion;
	}
	public int getMessageName() {
		return messageName;
	}
	public void setMessageName(int messageName) {
		this.messageName = messageName;
	}
	public long getMessageReference() {
		return messageReference;
	}
	public void setMessageReference(long messageReference) {
		this.messageReference = messageReference;
	}
	public long getMessageReferenceOA() {
		return messageReferenceOA;
	}
	public void setMessageReferenceOA(long messageReferenceOA) {
		this.messageReferenceOA = messageReferenceOA;
	}
	public int getMessageStatus() {
		return messageStatus;
	}
	public void setMessageStatus(int messageStatus) {
		this.messageStatus = messageStatus;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

    public String getMessageDescription() {
        return messageDescription;
    }

    public void setMessageDescription(String messageDescription) {
        this.messageDescription = messageDescription;
    }
}

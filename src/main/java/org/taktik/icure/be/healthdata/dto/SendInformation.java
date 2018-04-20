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

package org.taktik.icure.be.healthdata.dto;

import java.util.Map;

public class SendInformation {
	public Addressee addressee;
	public String subject;
	public Map<String,String> metadata;
	public Addressee encryptionAddressee;
	public String csvContent;

	public Addressee getAddressee() {
		return addressee;
	}

	public void setAddressee(Addressee addressee) {
		this.addressee = addressee;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public Addressee getEncryptionAddressee() {
		return encryptionAddressee;
	}

	public void setEncryptionAddressee(Addressee encryptionAddressee) {
		this.encryptionAddressee = encryptionAddressee;
	}

	public String getCsvContent() {
		return csvContent;
	}

	public void setCsvContent(String csvContent) {
		this.csvContent = csvContent;
	}
}

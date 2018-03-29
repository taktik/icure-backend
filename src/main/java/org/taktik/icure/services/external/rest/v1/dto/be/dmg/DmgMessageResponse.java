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

package org.taktik.icure.services.external.rest.v1.dto.be.dmg;

import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;

public class DmgMessageResponse {
	private DmgMessage dmgMessage;
	private MessageDto message;
	private DocumentDto document;

	public DmgMessage getDmgMessage() {
		return dmgMessage;
	}

	public void setDmgMessage(DmgMessage dmgMessage) {
		this.dmgMessage = dmgMessage;
	}

	public MessageDto getMessage() {
		return message;
	}

	public void setMessage(MessageDto message) {
		this.message = message;
	}

	public DocumentDto getDocument() {
		return document;
	}

	public void setDocument(DocumentDto document) {
		this.document = document;
	}
}

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

package org.taktik.icure.services.external.rest.v1.dto.be.efact;

import org.taktik.icure.services.external.rest.v1.dto.MessageDto;

public class SentMessageBatchDto {
	MessageDto message;
	EfactInvoiceResponseDto response;

	public SentMessageBatchDto(MessageDto message, EfactInvoiceResponseDto response) {
		this.message = message;
		this.response = response;
	}

	public MessageDto getMessage() {
		return message;
	}

	public void setMessage(MessageDto message) {
		this.message = message;
	}

	public EfactInvoiceResponseDto getResponse() {
		return response;
	}

	public void setResponse(EfactInvoiceResponseDto response) {
		this.response = response;
	}
}

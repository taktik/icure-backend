/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class EfactInvoiceResponseDto {
    private String inputReference;
    private boolean success;
    private MessageDto message;
	private DocumentDto document;

    public EfactInvoiceResponseDto() {
    }

    public EfactInvoiceResponseDto(boolean success, String inputReference, MessageDto message, DocumentDto document) {
        this.inputReference = inputReference;
        this.success = success;
		this.message = message;
		this.document = document;
	}

    public String getInputReference() {
        return inputReference;
    }

    public void setInputReference(String inputReference) {
        this.inputReference = inputReference;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

	public DocumentDto getDocument() {
		return document;
	}

	public void setDocument(DocumentDto document) {
		this.document = document;
	}

	public MessageDto getMessage() {
		return message;
	}

	public void setMessage(MessageDto message) {
		this.message = message;
	}
}

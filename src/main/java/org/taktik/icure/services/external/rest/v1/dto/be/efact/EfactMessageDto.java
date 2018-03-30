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

import java.io.Serializable;
import java.util.List;

import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.InvoiceDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 20/08/15
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class EfactMessageDto implements Serializable {

	private String detail;
	private String id;
	private String name;

	private MessageDto message;
	private DocumentDto document;
	private List<InvoiceDto> reassignedInvoices;


	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public List<InvoiceDto> getReassignedInvoices() {
		return reassignedInvoices;
	}

	public void setReassignedInvoices(List<InvoiceDto> reassignedInvoices) {
		this.reassignedInvoices = reassignedInvoices;
	}
}

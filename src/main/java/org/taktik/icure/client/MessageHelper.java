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

package org.taktik.icure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;

import java.io.IOException;

public class MessageHelper {

	private static Logger logger = LoggerFactory.getLogger(MessageHelper.class);

	private ICureHelper iCureHelper;

	public MessageHelper(ICureHelper iCureHelper) {
		this.iCureHelper = iCureHelper;
	}

	public MessageDto create(MessageDto messageDto) throws IOException {
		String response = iCureHelper.doRestPOST("message", messageDto);
		logger.info(response);
		return iCureHelper.getGson().fromJson(response, MessageDto.class);
	}

	public MessageDto create(MessageDto messageDto, DocumentDto documentDto) throws IOException {
		String responseForDocument = iCureHelper.doRestPOST("document", documentDto);
		logger.info(responseForDocument);
		DocumentDto createdDocumentDto = iCureHelper.getGson().fromJson(responseForDocument, DocumentDto.class);

		// TODO: re-check this, how is Message listing documents?
//		messageDto.setDocumentId(createdDocumentDto.getId());
		String responseForMessage = iCureHelper.doRestPOST("message", messageDto);
		logger.info(responseForMessage);
		return iCureHelper.getGson().fromJson(responseForMessage, MessageDto.class);
	}

	public String delete(String messageId) throws IOException {
		return iCureHelper.doRestPOST("message/delete/" + messageId, null);
	}

	public MessageDto get(String messageId) throws IOException {
		String response = iCureHelper.doRestGET("message/" + messageId);
		logger.info(response);
		return iCureHelper.getGson().fromJson(response, MessageDto.class);
	}
}

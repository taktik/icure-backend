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
package org.taktik.icure.services.external.rest.v1.dto.be.mikrono

/**
 * Created by aduchate on 12/07/2017.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.MimeAttachmentDto
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EmailOrSmsMessageDto(
        val attachments: List<MimeAttachmentDto> = listOf(),
        val destination //email or phone number (international format)
        : String? = null,
        val destinationIsNotPatient: Boolean = false, //Messages is sent to other patient's doctor but should appear in patient emails list and be highlighted.
        val destinationName // Case of a doc.
        : String? = null,
        val sendCopyToSender: Boolean = false,
        val senderName: String? = null,
        val replyToEmail: String? = null,
        val content: String? = null,
        val messageId: String? = null,
        val patientId: String? = null,
        val senderId: String? = null,
        val subject: String? = null,
        val type: Type? = null
) : Serializable

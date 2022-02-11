/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.rest.v2.dto.be.mikrono

/**
 * Created by aduchate on 12/07/2017.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.MimeAttachmentDto
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EmailOrSmsMessageDto(
        val attachments: List<MimeAttachmentDto> = emptyList(),
        val destination //email or phone number (international format)
        : String? = null,
        @Schema(defaultValue = "false") val destinationIsNotPatient: Boolean = false, //Messages is sent to other patient's doctor but should appear in patient emails list and be highlighted.
        val destinationName // Case of a doc.
        : String? = null,
        @Schema(defaultValue = "false") val sendCopyToSender: Boolean = false,
        val senderName: String? = null,
        val replyToEmail: String? = null,
        val content: String? = null,
        val messageId: String? = null,
        val patientId: String? = null,
        val senderId: String? = null,
        val subject: String? = null,
        val type: Type? = null
) : Serializable

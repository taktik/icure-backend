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
package org.taktik.icure.services.external.rest.v2.dto.embed

/**
 * Created by aduchate on 06/07/13, 10:09
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v2.dto.base.ICureDocumentDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity represents a sub-contact. It is serialized in JSON and saved in the underlying icure-contact CouchDB database.""")
data class SubContactDto(

        @Schema(description = "The Id of the sub-contact. We encourage using either a v4 UUID or a HL7 Id.") override val id: String? = null,
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        override val tags: Set<CodeStubDto> = setOf(),
        override val codes: Set<CodeStubDto> = setOf(),
        override val endOfLife: Long? = null,
        @Schema(description = "Description of the sub-contact") val descr: String? = null,
        @Schema(description = "Protocol based on which the sub-contact was used for linking services to structuring elements") val protocol: String? = null,
        val status: Int? = null, //To be refactored
        @Schema(description = "Id of the form used in the sub-contact. Several sub-contacts with the same form ID can coexist as long as they are in different contacts or they relate to a different planOfActionID") val formId: String? = null, // form or subform unique ID. Several subcontacts with the same form ID can coexist as long as they are in different contacts or they relate to a different planOfActionID
        @Schema(description = "Id of the plan of action (healthcare approach) that is linked by the sub-contact to a service.") val planOfActionId: String? = null,
        @Schema(description = "Id of the healthcare element that is linked by the sub-contact to a service") val healthElementId: String? = null,
        val classificationId: String? = null,
        @Schema(description = "List of all services provided to the patient under a given contact which is linked by this sub-contact to other structuring elements.") val services: List<ServiceLinkDto> = listOf(),
        override val encryptedSelf: String? = null
) : EncryptedDto, ICureDocumentDto<String?>

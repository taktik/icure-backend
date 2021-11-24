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
package org.taktik.icure.services.external.rest.v1.dto.embed

/**
 * Services are created in the course a contact. Information like temperature, blood pressure and so on.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifierDto
import org.taktik.icure.services.external.rest.v1.dto.base.LinkQualificationDto
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity represents a Service. A Service is created in the course a contact.
Services include subjective information provided by the patient, such as complaints, reason for visit, feelings, etc. or objective information like bio-metric measures (blood pressure, temperature, heart beat, etc.), or physical exam description, diagnosis, prescription, integration of lab reports from another healthcare party, action plan, etc.
Any action performed by the healthcare party which is relevant for the healthcare element of a patient is considered a service. The services can be linked to healthcare elements or other structuring elements of the medical record""")
data class ServiceDto(
        @Schema(description = "The Id of the Service. We encourage using either a v4 UUID or a HL7 Id.") override val id: String = UUID.randomUUID().toString(),//Only used when the ServiceDto is emitted outside of its contact
        val identifier: List<IdentifierDto> = listOf(),
        @Schema(description = "Id of the contact during which the service is provided") val contactId: String? = null,
        @Schema(description = "List of IDs of all sub-contacts that link the service to structural elements. Only used when the Service is emitted outside of its contact") val subContactIds: Set<String>? = null, //Only used when the ServiceDto is emitted outside of its contact
        @Schema(description = "List of IDs of all plans of actions (healthcare approaches) as a part of which the Service is provided. Only used when the Service is emitted outside of its contact") val plansOfActionIds: Set<String>? = null, //Only used when the ServiceDto is emitted outside of its contact
        @Schema(description = "List of IDs of all healthcare elements for which the service is provided. Only used when the Service is emitted outside of its contact") val healthElementsIds: Set<String>? = null, //Only used when the ServiceDto is emitted outside of its contact
        @Schema(description = "List of Ids of all forms linked to the Service. Only used when the Service is emitted outside of its contact.") val formIds: Set<String>? = null, //Only used when the ServiceDto is emitted outside of its contact
        @Schema(description = "The secret patient key, encrypted in the patient document, in clear here.") val secretForeignKeys: Set<String>? = HashSet(), //Only used when the ServiceDto is emitted outside of its contact
        @Schema(description = "The public patient key, encrypted here for separate Crypto Actors.") val cryptedForeignKeys: Map<String, Set<DelegationDto>> = mapOf(), //Only used when the ServiceDto is emitted outside of its contact
        @Schema(description = "The delegations giving access to connected healthcare information.") val delegations: Map<String, Set<DelegationDto>> = mapOf(), //Only used when the ServiceDto is emitted outside of its contact
        @Schema(description = "The contact secret encryption key used to encrypt the secured properties (like services for example), encrypted for separate Crypto Actors.") val encryptionKeys: Map<String, Set<DelegationDto>> = mapOf(), //Only used when the ServiceDto is emitted outside of its contact
        val label: String = "<invalid>",
        val dataClassName: String? = null,
        val index: Long? = null, //Used for sorting
        @Schema(description = "The type of the content recorded in the documents for the service") val content: Map<String, ContentDto> = mapOf(), //Localized, in the case when the service contains a document, the document id is the SerializableValue
        @get:Deprecated("use encryptedSelf instead") val encryptedContent: String? = null, //Crypted (AES+base64) version of the above, deprecated, use encryptedSelf instead
        val textIndexes: Map<String, String> = mapOf(), //Same structure as content but used for full text indexation
        @Schema(description = "") val valueDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        @Schema(description = "") val openingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        @Schema(description = "") val closingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
        @Schema(description = "") val formId: String? = null, //Used to group logically related services
        override val created: Long? = null,
        override val modified: Long? = null,
        override val endOfLife: Long? = null,
        override val author: String? = null, //userId
        override val responsible: String? = null, //healthcarePartyId
        override val medicalLocationId: String? = null,
        @Schema(description = "Text, comments on the Service provided") val comment: String? = null,
        val status: Int? = null, //bit 0: active/inactive, bit 1: relevant/irrelevant, bit2 : present/absent, ex: 0 = active,relevant and present
        @Schema(description = "List of invoicing codes") val invoicingCodes: Set<String> = setOf(),
        @Schema(description = "Links towards related services (possibly in other contacts)") val qualifiedLinks: Map<LinkQualificationDto, Map<String, String>> = mapOf(), //Links towards related services (possibly in other contacts)
        override val codes: Set<CodeStubDto> = setOf(), //stub object of the CodeDto used to qualify the content of the ServiceDto
        override val tags: Set<CodeStubDto> = setOf(), //stub object of the tag used to qualify the type of the ServiceDto
        override val encryptedSelf: String? = null
) : EncryptedDto, ICureDocumentDto<String>, Comparable<ServiceDto> {
    override fun compareTo(other: ServiceDto): Int {
        if (this == other) {
            return 0
        }
        var idx = if (index != null && other.index != null) index.compareTo(other.index) else 0
        if (idx != 0) return idx
        idx = id.compareTo(other.id)
        return if (idx != 0) idx else 1
    }
}

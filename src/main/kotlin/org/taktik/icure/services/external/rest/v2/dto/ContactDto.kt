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
package org.taktik.icure.services.external.rest.v2.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v2.dto.base.EncryptableDto
import org.taktik.icure.services.external.rest.v2.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v2.dto.base.IdentifierDto
import org.taktik.icure.services.external.rest.v2.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v2.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v2.dto.embed.SubContactDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(
	description = """This entity is a root-level object. It represents a contact. It is serialized in JSON and saved in the underlying icure-contact CouchDB database.
The contact is the entity that records the medical information about the patient chronologically. A visit to the patient's house, a consultation at the practice, a phone call between the patient and the healthcare party or integrating lab reports into the medical file are examples of when a contact can be recorded.
A contact can occur with or without direct interaction between the patient and the healthcare party. For example, when a healthcare party encodes data received from laboratory's test result, this is done in the absence of a patient.
A contact groups together pieces of information collected during one single event, for one single patient and for one or more healthcare parties. Patient's complaints, the diagnosis of a new problem, a surgical procedure, etc. are collected inside a contact.
The main sub-element of the contact is the service. Each atomic piece of information collected during a contact is a service and is stored inside the services list of a contact.
"""
)
data class ContactDto(
	@Schema(description = "the Id of the contact. We encourage using either a v4 UUID or a HL7 Id.") override val id: String,
	@Schema(description = "the revision of the contact in the database, used for conflict management / optimistic locking.") override val rev: String? = null,
	override val created: Long? = null,
	override val modified: Long? = null,
	override val author: String? = null,
	override val responsible: String? = null,
	override val medicalLocationId: String? = null,
	override val tags: Set<CodeStubDto> = emptySet(),
	override val codes: Set<CodeStubDto> = emptySet(),
	@Schema(description = "The identifiers of the Contact") val identifier: List<IdentifierDto> = emptyList(),
        override val endOfLife: Long? = null,
        override val deletionDate: Long? = null,

	@Schema(description = "Separate contacts can merged in one logical contact if they share the same groupId. When a contact must be split to selectively assign rights to healthcare parties, the split contacts all share the same groupId") val groupId: String? = null, // Several contacts can be combined in a logical contact if they share the same groupId
	@Schema(description = "The date (YYYYMMDDhhmmss) of the start of the contact.") val openingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
	@Schema(description = "The date (YYYYMMDDhhmmss) marking the end of the contact.") val closingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
	@Schema(description = "Description of the contact") val descr: String? = null,
	@Schema(description = "Location where the contact was recorded.") val location: String? = null,
	@Schema(description = "An external (from another source) id with no guarantee or requirement for unicity.") val externalId: String? = null,
	@Schema(description = "The type of encounter made for the contact") val encounterType: CodeStubDto? = null,
	@Schema(description = "Set of all sub-contacts recorded during the given contact. Sub-contacts are used to link services embedded inside this contact to healthcare elements, healthcare approaches and/or forms.") val subContacts: Set<SubContactDto> = emptySet(),
	@Schema(description = "Set of all services provided to the patient during the contact.") val services: Set<ServiceDto> = emptySet(),

	@get:Deprecated("Use responsible") val healthcarePartyId: String? = null, //Redundant... Should be responsible
	@get:Deprecated("Use groupId") val modifiedContactId: String? = null,

	override val secretForeignKeys: Set<String> = emptySet(),
	override val cryptedForeignKeys: Map<String, Set<DelegationDto>> = emptyMap(),
	override val delegations: Map<String, Set<DelegationDto>> = emptyMap(),
	override val encryptionKeys: Map<String, Set<DelegationDto>> = emptyMap(),
	override val encryptedSelf: String? = null
) : StoredDocumentDto, ICureDocumentDto<String>, EncryptableDto {
	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

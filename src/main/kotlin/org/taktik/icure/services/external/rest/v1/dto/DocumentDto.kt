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
package org.taktik.icure.services.external.rest.v1.dto


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.EncryptableDto
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentLocationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentStatusDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentTypeDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity is a root level object. It represents a Document. It is serialized in JSON and saved in the underlying CouchDB database.""")
data class DocumentDto(
        @Schema(description = "The Id of the document. We encourage using either a v4 UUID or a HL7 Id.") override val id: String,
        @Schema(description = "The revision of the document in the database, used for conflict management / optimistic locking.") override val rev: String? = null,
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        override val tags: Set<CodeStubDto> = setOf(),
        override val codes: Set<CodeStubDto> = setOf(),
        override val endOfLife: Long? = null,
        override val deletionDate: Long? = null,

        @Schema(description = "Reference in object store") val objectStoreReference: String? = null,
        @Schema(description = "Location of the document") val documentLocation: DocumentLocationDto? = null,
        @Schema(description = "The type of document, ex: admission, clinical path, document report,invoice, etc.") val documentType: DocumentTypeDto? = null,
        @Schema(description = "The status of the development of the document. Ex: Draft, finalized, reviewed, signed, etc.") val documentStatus: DocumentStatusDto? = null,
        @Schema(description = "When the document is stored in an external repository, this is the uri of the document in that repository") val externalUri: String? = null,
        @Schema(description = "The main Uniform Type Identifier of the document (https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/understanding_utis/understand_utis_conc/understand_utis_conc.html#//apple_ref/doc/uid/TP40001319-CH202-CHDHIJDE)") val mainUti: String? = null,
        @Schema(description = "Name of the document") val name: String? = null,
        @Schema(description = "The document version") val version: String? = null,
        @Schema(description = "Extra Uniform Type Identifiers") val otherUtis: Set<String> = setOf(),
        @Schema(description = "The ICureDocument (Form, Contact, ...) that has been used to generate the document") val storedICureDocumentId: String? = null, //The ICureDocumentDto (FormDto, ContactDto, ...) that has been used to generate the document
        @Schema(description = "A unique external id (from another external source).") val externalUuid: String? = null,
        @Schema(description = "Size of the document file") val size: Long? = null,
        @Schema(description = "Hashed version of the document") val hash: String? = null,
        @Schema(description = "Id of the contact during which the document was created") val openingContactId: String? = null,

        @Schema(description = "Id of attachment to this document") val attachmentId: String? = null,

        val encryptedAttachment: ByteArray? = null,
        val decryptedAttachment: ByteArray? = null,

        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<DelegationDto>> = mapOf(),
        override val delegations: Map<String, Set<DelegationDto>> = mapOf(),
        override val encryptionKeys: Map<String, Set<DelegationDto>> = mapOf(),

        override val encryptedSelf: String? = null
) : StoredDocumentDto, ICureDocumentDto<String>, EncryptableDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

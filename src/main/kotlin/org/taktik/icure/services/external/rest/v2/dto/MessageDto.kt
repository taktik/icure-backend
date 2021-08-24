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
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.EncryptableDto
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.MessageReadStatusDto
import org.taktik.icure.utils.DynamicInitializer

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MessageDto(
        override val id: String,
        override val rev: String? = null,
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        override val tags: Set<CodeStubDto> = setOf(),
        override val codes: Set<CodeStubDto> = setOf(),
        override val endOfLife: Long? = null,
        override val deletionDate: Long? = null,

        val fromAddress: String? = null,
        val fromHealthcarePartyId: String? = null,
        val formId: String? = null,
        val status: Int? = null,
        val recipientsType: String? = null,
        val recipients: Set<String> = setOf(), //The id of the hcp whose the message is addressed to
        val toAddresses: Set<String> = setOf(), //The address of the recipient of the message. Format is of an email address with extra domains defined for mycarenet and ehealth: (efact.mycarenet.be/eattest.mycarenet.be/chapter4.mycarenet.be/ehbox.ehealth.fgov.be)
        val received: Long? = null,
        val sent: Long? = null,
        val metas: Map<String, String> = mapOf(),
        val readStatus: Map<String, MessageReadStatusDto> = mapOf(),
        /*
            CHAP4:IN:   ${Mycarenet message ref}
            CHAP4:OUT:  ${Mycarenet message ref}
            EFACT:BATCH:${Mycarenet message ref}
            EFACT:IN:   ${Mycarenet message ref}
            EFACT:OUT:  ${Mycarenet message ref}
            GMD:IN:     ${Mycarenet message ref}
            INBOX:      ${Ehealth box message ref}
            SENTBOX:    ${Ehealth box message ref}
            BININBOX:   ${Ehealth box message ref}
            BINSENTBOX: ${Ehealth box message ref}
            REPORT:IN:  ${iCure ref}
            REPORT:OUT: ${iCure ref}
         */
        val transportGuid: String? = null, //Each message should have a transportGuid: see above for formats
        val remark: String? = null,
        val conversationGuid: String? = null,
        val subject: String? = null,
        val invoiceIds: Set<String> = setOf(),
        val parentId: String? = null, //ID of parent in a message conversation
        val externalRef: String? = null,
        val unassignedResults: Set<String> = setOf(), //refs
        val assignedResults: Map<String, String> = mapOf(), //ContactId -> ref
        val senderReferences: Map<String, String> = mapOf(),

        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<DelegationDto>> = mapOf(),
        override val delegations: Map<String, Set<DelegationDto>> = mapOf(),
        override val encryptionKeys: Map<String, Set<DelegationDto>> = mapOf(),
        override val encryptedSelf: String? = null
) : StoredDocumentDto, ICureDocumentDto<String>, EncryptableDto {
    companion object : DynamicInitializer<MessageDto> {
        const val STATUS_LABO_RESULT = 1 shl 0
        const val STATUS_UNREAD = 1 shl 1
        const val STATUS_IMPORTANT = 1 shl 2
        const val STATUS_ENCRYPTED = 1 shl 3
        const val STATUS_HAS_ANNEX = 1 shl 4
        const val STATUS_HAS_FREE_INFORMATION = 1 shl 5
        const val STATUS_EFACT = 1 shl 6
        const val STATUS_SENT = 1 shl 7
        const val STATUS_SUBMITTED = 1 shl 8 //tack
        const val STATUS_RECEIVED = 1 shl 9 //tack
        const val STATUS_ACCEPTED_FOR_TREATMENT = 1 shl 10 //931000
        const val STATUS_ACCEPTED = 1 shl 11 //920098 920900 920099
        const val STATUS_REJECTED = 1 shl 12 //920999
        const val STATUS_TACK = 1 shl 13
        const val STATUS_MASKED = 1 shl 14
        const val STATUS_FULL_SUCCESS = 1 shl 15 //920900 920098
        const val STATUS_PARTIAL_SUCCESS = 1 shl 16 //920900
        const val STATUS_FULL_ERROR = 1 shl 17 //920099 920999
        const val STATUS_ANALYZED = 1 shl 18
        const val STATUS_DELETED_ON_SERVER = 1 shl 19
        const val STATUS_SHOULD_BE_DELETED_ON_SERVER = 1 shl 20
        const val STATUS_ARCHIVED = 1 shl 21
        const val STATUS_ERRORS_IN_PRELIMINARY_CONTROL = 1 shl 22
        const val STATUS_DRAFT = 1 shl 23
        const val STATUS_SCANNED = 1 shl 24
        const val STATUS_IMPORTED = 1 shl 25
        const val STATUS_TREATED = 1 shl 26
    }

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

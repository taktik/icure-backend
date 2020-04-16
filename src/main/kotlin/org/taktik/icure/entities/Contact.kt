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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import org.ektorp.Attachment
import org.taktik.icure.entities.base.*
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.entities.utils.MergeUtil.mergeSets
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Contact(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String?,
        @NotNull(autoFix = AutoFix.NOW) override val created: Long?,
        @NotNull(autoFix = AutoFix.NOW) override val modified: Long?,
        @NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String?,
        @NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String?,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub>,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub>,
        override val endOfLife: Long?,
        @JsonProperty("deleted") override val deletionDate: Long?,

        @NotNull(autoFix = AutoFix.FUZZYNOW) val openingDate : Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        @NotNull(autoFix = AutoFix.UUID) val groupId : String? = null, // Several contacts can be combined in a logical contact if they share the same groupId

        val closingDate : Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val descr: String? = null,
        val location: String? = null,
        val healthcarePartyId: String? = null, //Redundant... Should be responsible
        val externalId: String? = null,
        val modifiedContactId: String? = null,
        val encounterType: Code? = null,
        val subContacts: @Valid Set<SubContact> = setOf(),
        val services: @Valid Set<Service> = setOf(),

        override val secretForeignKeys: Set<String>,
        override val cryptedForeignKeys: Map<String, Set<Delegation>>,
        override val delegations: Map<String, Set<Delegation>>,
        override val encryptionKeys: Map<String, Set<Delegation>>,
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>,
        @JsonProperty("_conflicts") override val conflicts: List<String>,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>,
        @JsonProperty("java_type") override val _type: String = Contact::javaClass.name
) : StoredDocument, ICureDocument, Encryptable {
    companion object : DynamicInitializer<Contact>

    fun solveConflictsWith(other: Contact) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
            "openingDate" to (openingDate?.coerceAtMost(other.openingDate ?: Long.MAX_VALUE) ?: other.openingDate),
            "closingDate" to (closingDate?.coerceAtLeast(other.closingDate ?: 0L) ?: other.closingDate),
            "descr" to (this.descr ?: other.descr),
            "location" to (this.location ?: other.location),
            "encounterType" to (this.encounterType ?: other.encounterType),
            "subContacts" to mergeSets(subContacts, other.subContacts, { a, b -> a.id == b.id },
                    { a: SubContact, b: SubContact -> a.merge(b) }),
            "services" to mergeSets(services, other.services, { a, b -> a.id == b.id },
                    { a: Service, b: Service -> a.merge(b) })
    )
}


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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.entities.utils.MergeUtil.mergeSets
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Contact(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = setOf(),
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = setOf(),
        override val endOfLife: Long? = null,
        @field:JsonProperty("deleted") override val deletionDate: Long? = null,

        @field:NotNull(autoFix = AutoFix.UUID) val groupId: String? = null, // Several contacts can be combined in a logical contact if they share the same groupId

        @field:NotNull(autoFix = AutoFix.FUZZYNOW) val openingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val closingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

        val descr: String? = null,
        val location: String? = null,
        @Deprecated("Replaced by responsible") val healthcarePartyId: String? = null, //Redundant and obsolete... Should be responsible
        val externalId: String? = null,
        val modifiedContactId: String? = null,
        val encounterType: CodeStub? = null,
        @field:Valid val subContacts: Set<SubContact> = setOf(),
        @field:Valid val services: Set<Service> = setOf(),

        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<Delegation>> = mapOf(),
        override val delegations: Map<String, Set<Delegation>> = mapOf(),
        override val encryptionKeys: Map<String, Set<Delegation>> = mapOf(),
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null
) : StoredICureDocument, Encryptable {
    companion object : DynamicInitializer<Contact>

    fun merge(other: Contact) = Contact(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Contact) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
            "openingDate" to (openingDate?.coerceAtMost(other.openingDate ?: Long.MAX_VALUE) ?: other.openingDate),
            "closingDate" to (closingDate?.coerceAtLeast(other.closingDate ?: 0L) ?: other.closingDate),
            "descr" to (this.descr ?: other.descr),
            "groupId" to (this.groupId ?: other.groupId),
            "healthcarePartyId" to (this.healthcarePartyId ?: other.healthcarePartyId),
            "externalId" to (this.externalId ?: other.externalId),
            "modifiedContactId" to (this.modifiedContactId ?: other.modifiedContactId),
            "location" to (this.location ?: other.location),
            "encounterType" to (this.encounterType ?: other.encounterType),
            "subContacts" to mergeSets(subContacts, other.subContacts, { a, b -> a.id == b.id },
                    { a: SubContact, b: SubContact -> a.merge(b) }),
            "services" to mergeSets(services, other.services, { a, b -> a.id == b.id },
                    { a: Service, b: Service -> a.merge(b) })
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
    override fun withTimestamps(created: Long?, modified: Long?) =
            when {
                created != null && modified != null -> this.copy(created = created, modified = modified)
                created != null -> this.copy(created = created)
                modified != null -> this.copy(modified = modified)
                else -> this
            }

}


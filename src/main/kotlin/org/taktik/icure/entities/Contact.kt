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

/**
 * This entity is a root level object. It represents a contact. It is serialized in JSON and saved in the underlying icure-contact CouchDB database.
 *
 * A contact is an entry in the day to day journal of the medical file of a patient. A contact happens between one patient, one or several healthcare parties (with one healthcare party promoted as the responsible of the contact), at one place during one (fairly short) period of time.
 * A contact contains a series of services (acts, observations, exchanges) performed on the patient. These services can be linked to healthcare elements

 * A Contact conforms to a series of interfaces:
 * - StoredICureDocument
 * - Encryptable
 *
 * @property id The Id of the contact. We encourage using either a v4 UUID or a HL7 Id.
 * @property rev The revision of the contact in the database, used for conflict management / optimistic locking.
 * @property created The timestamp (unix epoch in ms) of creation of the contact, will be filled automatically if missing. Not enforced by the application server.
 * @property modified The date (unix epoch in ms) of the latest modification of the contact, will be filled automatically if missing. Not enforced by the application server.
 * @property author The id of the User that has created this contact, will be filled automatically if missing. Not enforced by the application server.
 * @property responsible The id of the HealthcareParty that is responsible for this patient, will be filled automatically if missing. Not enforced by the application server.
 * @property medicalLocationId The id of the medical location where the contact was recorded.
 * @property tags Tags that qualify the contact as being member of a certain class.
 * @property codes Codes that identify or qualify this particular contact.
 * @property endOfLife Soft delete (unix epoch in ms) timestamp of the object.
 * @property deletionDate Hard delete (unix epoch in ms) timestamp of the object.
 * @property groupId Separate contacts can merged in one logical contact if they share the same groupId. When a contact must be split to selectively assign rights to healthcare parties, the split contacts all share the same groupId
 * @property openingDate The date (YYYYMMDDhhmmss) of the start of the contact.
 * @property deletionDate The date (YYYYMMDDhhmmss) marking the end of the contact.
 * @property descr Description of the contact
 * @property location Location where the contact was recorded.
 * @property externalId An external (from another source) id with no guarantee or requirement for unicity.
 * @property encounterType The type of encounter made for the contact
 * @property subContacts Set of all sub-contacts recorded during the given contact. Sub-contacts are used to link services embedded inside this contact to healthcare elements, healthcare approaches and/or forms.
 * @property services Set of all services provided to the patient during the contact.
 * @property delegations The delegations giving access to connected healthcare information.
 * @property secretForeignKeys The secret patient key, encrypted in the patient document, in clear here.
 * @property cryptedForeignKeys The public patient key, encrypted here for separate Crypto Actors.
 * @property encryptionKeys The contact secret encryption key used to encrypt the secured properties (like services for example), encrypted for separate Crypto Actors.
 * @property encryptedSelf The encrypted fields of this contact.
 *
 */

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
        @Deprecated("Contacts should be linked together using formId in subcontact") val modifiedContactId: String? = null,
        val encounterType: CodeStub? = null,
        @field:Valid val subContacts: Set<SubContact> = setOf(),
        @field:Valid val services: Set<Service> = setOf(),

        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<Delegation>> = mapOf(),
        override val delegations: Map<String, Set<Delegation>> = mapOf(),
        override val encryptionKeys: Map<String, Set<Delegation>> = mapOf(),
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = mapOf(),
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = listOf(),
        @JsonProperty("_conflicts") override val conflicts: List<String>? = listOf(),
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = mapOf()
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


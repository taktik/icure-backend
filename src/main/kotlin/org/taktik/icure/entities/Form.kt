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
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

/**
 * Created by aduchate on 18/07/13, 13:06
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder

/**
 * This entity is a root level object. It represents a Form. It is serialized in JSON and saved in the underlying CouchDB database.
 *
 * A form is used to visually structure medical information pertaining to one or several healthcare elements and collected on the course of a series of contacts.
 * Forms are organised in a hierarchy. They are the building blocks of a folder structure, of input forms or dashboards.
 * Contacts' services are linked to forms by means of the sub-contacts.
 *
 * A Form conforms to a series of interfaces:
 * - StoredICureDocument
 * - Encryptable
 *
 * @property id The Id of the form. We encourage using either a v4 UUID or a HL7 Id.
 * @property rev The revision of the form in the database, used for conflict management / optimistic locking.
 * @property created The timestamp (unix epoch in ms) of creation of the form, will be filled automatically if missing. Not enforced by the application server.
 * @property modified The date (unix epoch in ms) of the latest modification of the form, will be filled automatically if missing. Not enforced by the application server.
 * @property author The id of the User that has created this form, will be filled automatically if missing. Not enforced by the application server.
 * @property responsible The id of the healthcare party that is responsible for this form, will be filled automatically if missing. Not enforced by the application server.
 * @property medicalLocationId The id of the medical location where the form was created.
 * @property tags Tags that qualify the form as being member of a certain class.
 * @property codes Codes that identify or qualify this particular form.
 * @property endOfLife Soft delete (unix epoch in ms) timestamp of the object.
 * @property deletionDate Hard delete (unix epoch in ms) timestamp of the object.
 * @property descr Name/basic description of the form
 * @property formTemplateId Id of the form template being used to display the form
 * @property contactId Id of the contact for which the form is being used.
 * @property parent The parent of this form, used to determine the forms hierarchy
 * @property externalUuid A unique external id (from another external source).
 * @property delegations The delegations giving access to all connected healthcare information.
 * @property encryptionKeys The patient secret encryption key used to encrypt the secured properties (like note for example), encrypted for separate Crypto Actors.
 * @property encryptedSelf The encrypted fields of this Form.
 *
 */

data class Form(
	@JsonProperty("_id") override val id: String,
	@JsonProperty("_rev") override val rev: String? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
	override val medicalLocationId: String? = null,
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = emptySet(),
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = emptySet(),
	override val endOfLife: Long? = null,
	@JsonProperty("deleted") override val deletionDate: Long? = null,

	@field:NotNull(autoFix = AutoFix.FUZZYNOW) val openingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.

	val uniqueId: String? = null,
	val status: String? = null,
	val version: Int? = null,
	val logicalUuid: String? = null,
	val descr: String? = null,
	val formTemplateId: String? = null,
	val contactId: String? = null,
	@Deprecated("Use sub-contacts in contact") val healthElementId: String? = null,
	@Deprecated("Use sub-contacts in contact") val planOfActionId: String? = null,
	val parent: String? = null,

	override val secretForeignKeys: Set<String> = emptySet(),
	override val cryptedForeignKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val delegations: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptionKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptedSelf: String? = null,
	@JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
	@JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
	@JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
	@JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()

) : StoredICureDocument, Encryptable {
	companion object : DynamicInitializer<Form>

	fun merge(other: Form) = Form(args = this.solveConflictsWith(other))
	fun solveConflictsWith(other: Form) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
		"status" to (this.status ?: other.status),
		"version" to (this.version ?: other.version),
		"descr" to (this.descr ?: other.descr),
		"formTemplateId" to (this.formTemplateId ?: other.formTemplateId),
		"contactId" to (this.contactId ?: other.contactId),
		"uniqueId" to (this.uniqueId ?: other.uniqueId),
		"logicalUuid" to (this.logicalUuid ?: other.logicalUuid),
		"healthElementId" to (this.healthElementId ?: other.healthElementId),
		"planOfActionId" to (this.planOfActionId ?: other.planOfActionId),
		"parent" to (this.parent ?: other.parent)
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

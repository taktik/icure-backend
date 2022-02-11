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
import org.taktik.icure.entities.base.*
import org.taktik.icure.entities.embed.*
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder

/**
 * A Device
 *
 * This entity is a root level object. It represents a device, sending medical data. It is serialized in JSON and saved
 * in the underlying icure-healthdata CouchDB database.
 * A Device conforms to a series of interfaces:
 * - StoredDocument
 * - Named
 * - DataOwner
 * - CryptoActor
 *
 * @property id the Id of the device. We encourage using either a v4 UUID or a HL7 Id.
 * @property rev the revision of the device in the database, used for conflict management / optimistic locking.
 * @property created The timestamp (unix epoch in ms) of creation of the device. Enforced by the application server : will be filled automatically if missing.
 * @property modified the date (unix epoch in ms) of latest modification of the device. Enforced by the application server : will be filled automatically if missing.
 * @property author the id of the User that has created this device. Enforced by the application server : will be filled automatically if missing.
 * @property responsible the id of the HealthcareParty that is responsible for this device. Enforced by the application server : will be filled automatically if missing.
 * @property medicalLocationId the medical location where this device has been created. Not used for now.
 * @property tags tags that qualify the device as being member of a certain class.
 * @property codes codes that identify or qualify this particular device.
 * @property endOfLife soft delete (unix epoch in ms) timestamp of the object. Unused for device.
 * @property deletionDate hard delete (unix epoch in ms) timestamp of the object.
 * @property name The full name of the device, used mainly when the device is an organization
 * @property parentId Id of parent of the user representing the device.
 * @property picture A picture usually saved in JPEG format.
 * @property hcPartyKeys When a device has access to the medical file for modification or has been given access to it (any time he/she acts as a Crypto Actor), the list of exchange keys with other healthcare parties.
 * @property privateKeyShamirPartitions A set of shamir partitions for this device RSA private keys, encrypted with the public keys of the notaries (referred by their ids). Format is hcpId of key that has been partitioned : "threshold|partition in hex"
 * @property publicKey The public RSA key of this device
 * @property externalId An non-official id for the device. This one is not guaranteed to be unique in databases.
 * @property name Name of the device.
 * @property type Type of the device. Could be a smartphone, or a specific medical type sort, ...
 * @property brand Brand of the device (Samsung, Apple, Philips, ...)
 * @property model Model of the device (Galaxy S10, Kino.md, ...)
 * @property serialNumber Serial number of the device
 * @property properties List of typed properties related to the device. Could be its version, specific device information, ...
 * @property identifiers The device's identifiers. Those identifiers are the ones identifying the device for the client.
 */

data class Device(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        @field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        override val endOfLife: Long? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = emptySet(),
        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = emptySet(),

        val externalId: String? = null,
        val identifiers: List<Identifier> = emptyList(),

        override val name: String? = null,
        val type: String? = null,
        val brand: String? = null,
        val model: String? = null,
        val serialNumber: String? = null,

        val parentId: String? = null,
        val picture: ByteArray? = null,

        override val properties: Set<PropertyStub> = emptySet(),

        // One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
        // For a pair of HcParties, this key is called the AES exchange key
        // Each HcParty always has one AES exchange key for himself
        // The map's keys are the delegate id.
        // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
        // the key encrypted using delegate's public key.
        override val hcPartyKeys: Map<String, Array<String>> = emptyMap(),
        override val privateKeyShamirPartitions: Map<String, String> = emptyMap(), //Format is hcpId of key that has been partitioned : "threshold|partition in hex"
        override val publicKey: String? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
        @JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()

) : StoredICureDocument, Named, CryptoActor, DataOwner {
    companion object : DynamicInitializer<Device>

    fun merge(other: Device) = HealthcareParty(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Device) = super<StoredICureDocument>.solveConflictsWith(other) + super<CryptoActor>.solveConflictsWith(other) + super<DataOwner>.solveConflictsWith(other) + mapOf(
            "parentId" to (this.parentId ?: other.parentId),
            "picture" to (this.picture ?: other.picture),
            "externalId" to (this.type ?: other.externalId),
            "type" to (this.type ?: other.type),
            "brand" to (this.type ?: other.brand),
            "model" to (this.type ?: other.model),
            "serialNumber" to (this.type ?: other.serialNumber),
            "identifier" to mergeListsDistinct(this.identifiers, other.identifiers,
                    { a, b -> a.system == b.system && a.value == b.value },
            ),
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

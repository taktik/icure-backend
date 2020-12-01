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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.constants.Users
import org.taktik.icure.entities.base.Principal
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.DelegationTag
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil.mergeMapsOfSetsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
/**
 *
 * This entity is a root level object. It represents an user that can log in to the iCure platform. It is serialized in JSON and saved in the underlying icure-base CouchDB database.
 * A User conforms to a series of interfaces:
 * - StoredDocument
 * - Principal
 *
 * @property id the Id of the patient. We encourage using either a v4 UUID or a HL7 Id.
 * @property rev the revision of the patient in the database, used for conflict management / optimistic locking.
 * @property created the timestamp (unix epoch in ms) of creation of the patient, will be filled automatically if missing. Not enforced by the application server.
 * @property deletionDate hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called.
 * @property name
 * @property properties
 * @property roles
 * @property permissions
 * @property type
 * @property status
 * @property login
 * @property passwordHash
 * @property secret
 *
 */
data class User(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,
        @field:NotNull(autoFix = AutoFix.NOW) val created: Long? = null,

        override val name: String? = null,
        override val properties: Set<PropertyStub> = setOf(),
        val roles: Set<String> = setOf(),
        override val permissions: Set<Permission> = setOf(),
        val type: Users.Type? = null,
        val status: Users.Status? = null,
        val login: String? = null,
        val passwordHash: String? = null,
        val secret: String? = null,
        @JsonProperty("isUse2fa") val use2fa: Boolean? = null,
        val groupId: String? = null,
        val healthcarePartyId: String? = null,
        val patientId: String? = null,
        val autoDelegations: Map<DelegationTag, Set<String>> = mapOf(), //DelegationTag -> healthcarePartyIds
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val createdDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val lastLoginDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val expirationDate: Instant? = null,
        val activationToken: String? = null,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val activationTokenExpirationDate: Instant? = null,
        val passwordToken: String? = null,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val passwordTokenExpirationDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val termsOfUseDate: Instant? = null,

        val email: String? = null,
        val applicationTokens: Map<String, String> = mapOf(),

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null

) : StoredDocument, Principal, Cloneable, Serializable {
    companion object : DynamicInitializer<User>

    fun merge(other: User) = User(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: User) = super.solveConflictsWith(other) + mapOf(
            "created" to (this.created?.coerceAtMost(other.created ?: Long.MAX_VALUE) ?: other.created),
            "name" to (this.name ?: other.name),
            "properties" to (other.properties + this.properties),
            "permissions" to (other.permissions + this.permissions),
            "type" to (this.type ?: other.type),
            "status" to (this.status ?: other.status),
            "login" to (this.login ?: other.login),
            "passwordHash" to (this.passwordHash ?: other.passwordHash),
            "secret" to (this.secret ?: other.secret),
            "isUse2fa" to (this.use2fa ?: other.use2fa),
            "groupId" to (this.groupId ?: other.groupId),
            "healthcarePartyId" to (this.healthcarePartyId ?: other.healthcarePartyId),
            "patientId" to (this.patientId ?: other.patientId),
            "autoDelegations" to mergeMapsOfSetsDistinct(this.autoDelegations, other.autoDelegations),
            "createdDate" to (this.createdDate ?: other.createdDate),
            "lastLoginDate" to (this.lastLoginDate ?: other.lastLoginDate),
            "expirationDate" to (this.expirationDate ?: other.expirationDate),
            "activationToken" to (this.activationToken ?: other.activationToken),
            "activationTokenExpirationDate" to (this.activationTokenExpirationDate
                    ?: other.activationTokenExpirationDate),
            "passwordToken" to (this.passwordToken ?: other.passwordToken),
            "passwordTokenExpirationDate" to (this.passwordTokenExpirationDate ?: other.passwordTokenExpirationDate),
            "termsOfUseDate" to (this.termsOfUseDate ?: other.termsOfUseDate),
            "email" to (this.email ?: other.email),
            "applicationTokens" to (other.applicationTokens + this.applicationTokens)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)

    @JsonIgnore
    override fun getParents(): Set<String> = this.roles
}

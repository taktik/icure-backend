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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.ektorp.Attachment
import org.taktik.icure.constants.Users
import org.taktik.icure.entities.base.Principal
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.DelegationTag
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil.mergeMapsOfSetsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import org.taktik.icure.utils.invoke
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        override val name: String? = null,
        override val properties: Set<Property> = setOf(),
        override val permissions: Set<Permission> = setOf(),
        val type: Users.Type? = null,
        val status: Users.Status? = null,
        val login: String? = null,
        val passwordHash: String? = null,
        val secret: String? = null,
        val isUse2fa: Boolean? = null,
        val groupId: String? = null,
        val healthcarePartyId: String? = null,
        val patientId: String? = null,
        val autoDelegations: Map<DelegationTag, Set<String>> = mapOf(), //DelegationTag -> healthcarePartyIds
        @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val createdDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val lastLoginDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val expirationDate: Instant? = null,
        val activationToken: String? = null,

        @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val activationTokenExpirationDate: Instant? = null,
        val passwordToken: String? = null,

        @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val passwordTokenExpirationDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val termsOfUseDate: Instant? = null,

        val email: String? = null,
        val applicationTokens: Map<String, String> = mapOf(),

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = User::javaClass.name
) : StoredDocument, Principal, Cloneable, Serializable {
    companion object : DynamicInitializer<User>
    fun merge(other: User) = User(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: User) = super.solveConflictsWith(other) + mapOf(
        "name" to (this.name ?: other.name),
        "properties" to (other.properties + this.properties),
        "permissions" to (other.permissions + this.permissions),
        "type" to (this.type ?: other.type),
        "status" to (this.status ?: other.status),
        "login" to (this.login ?: other.login),
        "passwordHash" to (this.passwordHash ?: other.passwordHash),
        "secret" to (this.secret ?: other.secret),
        "isUse2fa" to (this.isUse2fa ?: other.isUse2fa),
        "groupId" to (this.groupId ?: other.groupId),
        "healthcarePartyId" to (this.healthcarePartyId ?: other.healthcarePartyId),
        "patientId" to (this.patientId ?: other.patientId),
        "autoDelegations" to mergeMapsOfSetsDistinct(this.autoDelegations, other.autoDelegations),
        "createdDate" to (this.createdDate ?: other.createdDate),
        "lastLoginDate" to (this.lastLoginDate ?: other.lastLoginDate),
        "expirationDate" to (this.expirationDate ?: other.expirationDate),
        "activationToken" to (this.activationToken ?: other.activationToken),
        "activationTokenExpirationDate" to (this.activationTokenExpirationDate ?: other.activationTokenExpirationDate),
        "passwordToken" to (this.passwordToken ?: other.passwordToken),
        "passwordTokenExpirationDate" to (this.passwordTokenExpirationDate ?: other.passwordTokenExpirationDate),
        "termsOfUseDate" to (this.termsOfUseDate ?: other.termsOfUseDate),
        "email" to (this.email ?: other.email),
        "applicationTokens" to (other.applicationTokens + this.applicationTokens)
    )
    override fun withIdRev(id: String?, rev: String): User =
            if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)

}

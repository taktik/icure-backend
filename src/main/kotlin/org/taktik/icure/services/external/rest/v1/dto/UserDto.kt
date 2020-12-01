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
package org.taktik.icure.services.external.rest.v1.dto


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.constants.Users
import org.taktik.icure.services.external.rest.v1.dto.base.PrincipalDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationTagDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PermissionDto
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class UserDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,
        val created: Long? = null,

        override val name: String? = null,
        override val properties: Set<PropertyStubDto> = setOf(),
        override val permissions: Set<PermissionDto> = setOf(),
        val roles: Set<String> = setOf(),
        val type: Users.Type? = null,
        val status: Users.Status? = null,
        val login: String? = null,
        val passwordHash: String? = null,
        val secret: String? = null,
        val use2fa: Boolean? = null,
        val groupId: String? = null,
        val healthcarePartyId: String? = null,
        val patientId: String? = null,
        val autoDelegations: Map<DelegationTagDto, Set<String>> = mapOf(), //DelegationTagDto -> healthcarePartyIds
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
        val applicationTokens: Map<String, String> = mapOf()
) : StoredDocumentDto, PrincipalDto, Cloneable, Serializable {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

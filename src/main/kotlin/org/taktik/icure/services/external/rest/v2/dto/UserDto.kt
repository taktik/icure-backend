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


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.constants.Users
import org.taktik.icure.services.external.rest.v2.dto.base.PrincipalDto
import org.taktik.icure.services.external.rest.v2.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationTagDto
import org.taktik.icure.services.external.rest.v2.dto.security.PermissionDto
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity is a root level object. It represents an user that can log in to the iCure platform. It is serialized in JSON and saved in the underlying icure-base CouchDB database.""")
data class UserDto(
        @Schema(description = "the Id of the user. We encourage using either a v4 UUID or a HL7 Id.") override val id: String,
        @Schema(description = "the revision of the user in the database, used for conflict management / optimistic locking.") override val rev: String? = null,
        override val deletionDate: Long? = null,
        val created: Long? = null,

        @Schema (description = "Last name of the user. This is the official last name that should be used for official administrative purposes.") override val name: String? = null,
        @Schema (description = "Extra properties for the user. Those properties are typed (see class Property)") override val properties: Set<PropertyStubDto> = emptySet(),
        @Schema (description = "If permission to modify patient data is granted or revoked") override val permissions: Set<PermissionDto> = emptySet(),
        @Schema (description = "Roles specified for the user") val roles: Set<String> = emptySet(),
        @Schema (description = "Authorization source for user. 'Database', 'ldap' or 'token'") val type: Users.Type? = null,
        @Schema (description = "State of user's activeness: 'Active', 'Disabled' or 'Registering'") val status: Users.Status? = null,
        @Schema (description = "Username for this user. We encourage using an email address") val login: String? = null,
        @Schema (description = "Hashed version of the password (BCrypt is used for hashing)") val passwordHash: String? = null,
        @Schema (description = "Secret token used to verify 2fa") val secret: String? = null,
        @Schema (description = "Whether the user has activated two factors authentication") val use2fa: Boolean? = null,
        @Schema (description = "id of the group (practice/hospital) the user is member of") val groupId: String? = null,
        @Schema (description = "Id of the healthcare party if the user is a healthcare party.") val healthcarePartyId: String? = null,
        @Schema (description = "Id of the patient if the user is a patient") val patientId: String? = null,
        @Schema (description = "Delegations that are automatically generated client side when a new database object is created by this user") val autoDelegations: Map<DelegationTagDto, Set<String>> = emptyMap(), //DelegationTagDto -> healthcarePartyIds

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        @Schema(description = "the timestamp (unix epoch in ms) of creation of the user, will be filled automatically if missing. Not enforced by the application server.") val createdDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        @Schema(description = "the timestamp (unix epoch in ms) of the latest validation of the terms of use of the application") val termsOfUseDate: Instant? = null,
        @Schema(description = "email address of the user.") val email: String? = null,
        @Schema(description = "Long lived authentication tokens used for inter-applications authentication.") val applicationTokens: Map<String, String> = emptyMap()
) : StoredDocumentDto, PrincipalDto, Cloneable, Serializable {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
    @JsonIgnore
    override fun getParents(): Set<String> = this.roles
}

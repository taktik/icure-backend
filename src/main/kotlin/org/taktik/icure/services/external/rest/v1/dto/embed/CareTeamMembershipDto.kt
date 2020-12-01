package org.taktik.icure.services.external.rest.v1.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CareTeamMembershipDto(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val careTeamMemberId: String? = null,
        val membershipType: MembershipTypeDto? = null,
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable

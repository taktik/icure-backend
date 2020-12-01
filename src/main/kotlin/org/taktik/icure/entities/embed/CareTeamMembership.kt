package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CareTeamMembership(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val careTeamMemberId: String? = null,
        val membershipType: MembershipType? = null,
        override val encryptedSelf: String? = null
) : Encrypted, Serializable

package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class CareTeamMembership(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val careTeamMemberId: String? = null,
        val membershipType: MembershipType? = null
) : Serializable

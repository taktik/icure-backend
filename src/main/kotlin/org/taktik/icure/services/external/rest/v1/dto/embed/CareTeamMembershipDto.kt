package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable


data class CareTeamMembershipDto(
        val startDate: Long? = null,
        val endDate: Long? = null,
        val careTeamMemberId: String? = null,
        val membershipType: MembershipTypeDto? = null,
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable

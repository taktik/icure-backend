package org.taktik.icure.entities.embed

import java.io.Serializable
import java.util.Objects

class CareTeamMembership : Serializable {
    var startDate: Long? = null
    var endDate: Long? = null
    var careTeamMemberId: String? = null
    var membershipType: MembershipType? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as CareTeamMembership
        return startDate == that.startDate &&
                endDate == that.endDate &&
                careTeamMemberId == that.careTeamMemberId && membershipType == that.membershipType
    }

    override fun hashCode(): Int {
        return Objects.hash(startDate, endDate, careTeamMemberId, membershipType)
    }

}

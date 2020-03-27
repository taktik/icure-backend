package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable
import java.util.Objects

class CareTeamMember : Serializable {
    var id: String? = null
    var careTeamMemberType: CareTeamMemberType? = null
    var healthcarePartyId: String? = null
    var quality: CodeStub? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is CareTeamMember) return false
        val that = o
        return id == that.id && careTeamMemberType == that.careTeamMemberType &&
                healthcarePartyId == that.healthcarePartyId &&
                quality == that.quality
    }

    override fun hashCode(): Int {
        return Objects.hash(id, careTeamMemberType, healthcarePartyId, quality)
    }
}

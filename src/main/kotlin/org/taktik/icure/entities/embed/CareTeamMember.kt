package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class CareTeamMember(
        @JsonProperty("_id") override val id: String,
        val careTeamMemberType: CareTeamMemberType? = null,
        val healthcarePartyId: String? = null,
        val quality: CodeStub? = null
) : Serializable, Identifiable<String> {
    companion object : DynamicInitializer<CareTeamMember>
    fun merge(other: CareTeamMember) = CareTeamMember(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: CareTeamMember) = mapOf(
            "id" to (this.id),
            "careTeamMemberType" to (this.careTeamMemberType ?: other.careTeamMemberType),
            "healthcarePartyId" to (this.healthcarePartyId ?: other.healthcarePartyId),
            "quality" to (this.quality ?: other.quality)
    )
}

package org.taktik.icure.services.external.rest.v1.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifiableDto
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CareTeamMemberDto(
        override val id: String,
        val careTeamMemberType: CareTeamMemberTypeDto? = null,
        val healthcarePartyId: String? = null,
        val quality: CodeStubDto? = null,
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable, IdentifiableDto<String> {
    companion object : DynamicInitializer<CareTeamMemberDto>

    fun merge(other: CareTeamMemberDto) = CareTeamMemberDto(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: CareTeamMemberDto) = super.solveConflictsWith(other) + mapOf(
            "id" to (this.id),
            "careTeamMemberType" to (this.careTeamMemberType ?: other.careTeamMemberType),
            "healthcarePartyId" to (this.healthcarePartyId ?: other.healthcarePartyId),
            "quality" to (this.quality ?: other.quality)
    )
}

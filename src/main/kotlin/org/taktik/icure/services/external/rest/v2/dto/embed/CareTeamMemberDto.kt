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

package org.taktik.icure.services.external.rest.v2.dto.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v2.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v2.dto.base.IdentifiableDto
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

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

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

package org.taktik.icure.services.external.rest.v2.dto.samv2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v2.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v2.dto.samv2.embed.AmpComponentDto
import org.taktik.icure.services.external.rest.v2.dto.samv2.embed.AmpStatusDto
import org.taktik.icure.services.external.rest.v2.dto.samv2.embed.AmppDto
import org.taktik.icure.services.external.rest.v2.dto.samv2.embed.CompanyDto
import org.taktik.icure.services.external.rest.v2.dto.samv2.embed.MedicineTypeDto
import org.taktik.icure.services.external.rest.v2.dto.samv2.embed.SamTextDto
import org.taktik.icure.services.external.rest.v2.dto.samv2.stub.VmpStubDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AmpDto(
	override val id: String,
	override val rev: String? = null,
	override val deletionDate: Long? = null,

	val from: Long? = null,
	val to: Long? = null,
	val code: String? = null,
	val vmp: VmpStubDto? = null,
	val officialName: String? = null,
	val status: AmpStatusDto? = null,
	val name: SamTextDto? = null,
	val blackTriangle: Boolean = false,
	val medicineType: MedicineTypeDto? = null,
	val company: CompanyDto? = null,
	val abbreviatedName: SamTextDto? = null,
	val proprietarySuffix: SamTextDto? = null,
	val prescriptionName: SamTextDto? = null,
	val ampps: List<AmppDto> = emptyList(),
	val components: List<AmpComponentDto> = emptyList()
) : StoredDocumentDto {
	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

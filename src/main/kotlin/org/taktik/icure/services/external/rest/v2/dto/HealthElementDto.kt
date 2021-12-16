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
package org.taktik.icure.services.external.rest.v2.dto


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.*
import org.taktik.icure.services.external.rest.v2.dto.embed.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity is a root level object. It represents a healthcare element. It is serialized in JSON and saved in the underlying CouchDB database.""")
data class HealthElementDto(
        @Schema(description = "The Id of the healthcare element. We encourage using either a v4 UUID or a HL7 Id.") override val id: String,
        val identifier: List<IdentifierDto> = listOf(),
        @Schema(description = "The revision of the patient in the database, used for conflict management / optimistic locking.") override val rev: String? = null,
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        override val tags: Set<CodeStubDto> = emptySet(),
        override val codes: Set<CodeStubDto> = emptySet(),
        override val endOfLife: Long? = null,
        override val deletionDate: Long? = null,

        @Schema(description = "The logical id of the healthcare element, used to link together different versions of the same healthcare element. We encourage using either a v4 UUID or a HL7 Id.") val healthElementId: String? = null,
        //Usually one of the following is used (either valueDate or openingDate and closingDate)
        @Schema(description = "The date (unix epoch in ms) when the healthcare element is noted to have started and also closes on the same date") val valueDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        @Schema(description = "The date (unix epoch in ms) of the start of the healthcare element.") val openingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        @Schema(description = "The date (unix epoch in ms) marking the end of the healthcare element.") val closingDate: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        @Schema(description = "Description of the healthcare element.") val descr: String? = null,
        @Schema(description = "A text note (can be confidential, encrypted by default).") val note: String? = null,
        @Schema(description = "If the healthcare element is relevant or not (Set relevant by default).") val relevant: Boolean = true,
        @Schema(description = "Id of the opening contact when the healthcare element was created.") val idOpeningContact: String? = null,
        @Schema(description = "Id of the closing contact for the healthcare element.") val idClosingContact: String? = null,
        @Schema(description = "Id of the service when a service is used to create a healthcare element.") val idService: String? = null, //When a service is used to create the healthElement
        @Schema(description = "bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present") val status: Int = 0, //bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present
        @Schema(description = "Left or Right dominance/preference.") val laterality: LateralityDto? = null,
        @Schema(description = "List of healthcare approaches.") val plansOfAction: List<PlanOfActionDto> = emptyList(),
        @Schema(description = "List of episodes of occurrences of the healthcare element.") val episodes: List<EpisodeDto> = emptyList(),
        @Schema(description = "List of care team members assigned for the healthcare element.") val careTeam: List<CareTeamMemberDto> = emptyList(),

        override val secretForeignKeys: Set<String> = emptySet(),
        override val cryptedForeignKeys: Map<String, Set<DelegationDto>> = emptyMap(),
        override val delegations: Map<String, Set<DelegationDto>> = emptyMap(),
        override val encryptionKeys: Map<String, Set<DelegationDto>> = emptyMap(),
        override val encryptedSelf: String? = null
) : StoredDocumentDto, ICureDocumentDto<String>, EncryptableDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

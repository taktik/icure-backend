package org.taktik.icure.services.external.rest.v1.dto.samv2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AmpComponentDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AmpStatusDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AmppDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.CompanyDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.MedicineTypeDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SamTextDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.stub.VmpStubDto

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
        val ampps: List<AmppDto> = listOf(),
        val components: List<AmpComponentDto> = listOf()
) : StoredDocumentDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

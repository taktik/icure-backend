package org.taktik.icure.services.external.rest.v1.dto.be.samv2

import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.*

class AmpDto(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var vmpCode: String? = null,
        var officialName: String? = null,
        var status: AmpStatusDto? = null,
        var name: SamTextDto? = null,
        var blackTriangle: Boolean = false,
        var medicineType: MedicineTypeDto? = null,
        var company: CompanyDto? = null,
        var abbreviatedName: SamTextDto? = null,
        var proprietarySuffix: SamTextDto? = null,
        var prescriptionName: SamTextDto? = null,
        var ampps : List<AmppDto> = listOf(),
        var components: List<AmpComponentDto> = listOf()
) : StoredDocumentWithPeriodDto(from, to)


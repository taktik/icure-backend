package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.samv2.embed.*
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.MedicineTypeDto

class Amp(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var vmpCode: String? = null,
        var officialName: String? = null,
        var status: AmpStatus? = null,
        var name: SamText? = null,
        var blackTriangle: Boolean = false,
        var medicineType: MedicineTypeDto? = null,
        var company: Company? = null,
        var abbreviatedName: SamText? = null,
        var proprietarySuffix: SamText? = null,
        var prescriptionName: SamText? = null,
        var ampps : List<Ampp> = listOf(),
        var components: List<AmpComponent> = listOf()
) : StoredDocumentWithPeriod(from, to)


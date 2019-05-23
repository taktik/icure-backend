package org.taktik.icure.services.external.rest.v1.dto.be.samv2

import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.*

import java.io.Serializable

class VmpDto(
        from: Long? = null,
        to: Long? = null,
        var name: SamTextDto? = null,
        var abbreviation: SamTextDto? = null,
        var vmpGroupId: String? = null,
        var vtm: VtmDto? = null,
        var wadas: List<WadaDto>? = null
) : StoredDocumentWithPeriodDto(from, to), Serializable

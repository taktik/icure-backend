package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class VtmDto(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamTextDto? = null
) : DataPeriodDto(from, to), Serializable

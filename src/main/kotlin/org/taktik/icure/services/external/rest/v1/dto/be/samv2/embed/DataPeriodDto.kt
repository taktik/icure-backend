package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

open class DataPeriodDto(
        var from: Long? = null,
        var to: Long? = null
) : Serializable

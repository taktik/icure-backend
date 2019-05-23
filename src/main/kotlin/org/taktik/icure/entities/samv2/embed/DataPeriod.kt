package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

open class DataPeriod(
        var from: Long? = null,
        var to: Long? = null
) : Serializable

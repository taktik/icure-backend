package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

interface DataPeriod : Serializable {
    val from: Long?
    val to: Long?
}

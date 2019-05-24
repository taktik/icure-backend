package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class Vtm(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamText? = null
) : DataPeriod(from, to), Serializable

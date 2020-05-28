package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class Vtm(
        override val from: Long? = null,
        override val to: Long? = null,
        val code: String? = null,
        val name: SamText? = null
) : DataPeriod

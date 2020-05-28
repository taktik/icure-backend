package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class Commercialization(
        override val from: Long? = null,
        override val to: Long? = null
) : DataPeriod

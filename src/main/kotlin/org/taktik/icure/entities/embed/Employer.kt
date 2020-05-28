package org.taktik.icure.entities.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.Named
import java.io.Serializable

@KotlinBuilder
data class Employer(
        override val name: String? = null,
        val addresse: Address? = null
) : Named, Serializable

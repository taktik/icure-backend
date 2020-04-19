package org.taktik.icure.entities.embed

import org.taktik.icure.entities.base.Named
import java.io.Serializable

data class Employer(
        override val name: String? = null,
        val addresse: Address? = null
) : Named, Serializable

package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class SamText(
        val fr: String? = null,
        val nl: String? = null,
        val de: String? = null,
        val en: String? = null
) : Serializable

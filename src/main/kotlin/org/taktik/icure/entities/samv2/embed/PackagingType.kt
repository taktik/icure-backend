package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class PackagingType(
        val code: String? = null,
        val name: SamText? = null,
        val edqmCode: String? = null,
        val edqmDefinition: String? = null
) : Serializable

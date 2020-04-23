package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class Wada(val code: String? = null, val name: SamText? = null, val description: SamText? = null) : Serializable

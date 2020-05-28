package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class NoSwitchReason(val code: String? = null, val description: SamText? = null) : Serializable

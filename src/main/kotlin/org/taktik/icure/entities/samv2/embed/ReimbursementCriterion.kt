package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class ReimbursementCriterion(val category: String? = null, val code: String? = null, val description: SamText? = null) : Serializable

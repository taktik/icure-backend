package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class ReimbursementCriterion(val category: String? = null, val code: String? = null, val description: SamText? = null) : Serializable

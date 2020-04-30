package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

data class PharmaceuticalForm(val code: String? = null, val name: SamText? = null, val standardForms: List<CodeStub> = listOf()) : Serializable

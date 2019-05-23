package org.taktik.icure.entities.samv2.embed

import org.taktik.icure.entities.base.Code

class PharmaceuticalForm(var name: SamText? = null, var standardForms: List<Code> = listOf())

package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import org.taktik.icure.entities.base.Code

class PharmaceuticalFormDto(var name: SamTextDto? = null, var standardForms: List<Code> = listOf())

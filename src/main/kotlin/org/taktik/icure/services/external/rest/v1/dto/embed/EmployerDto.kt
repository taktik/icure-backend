package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
import java.io.Serializable

data class EmployerDto(
        override val name: String? = null,
        val addresse: AddressDto? = null
) : NamedDto, Serializable

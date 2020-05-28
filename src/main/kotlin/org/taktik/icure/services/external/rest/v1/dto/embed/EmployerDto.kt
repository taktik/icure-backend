package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
import java.io.Serializable

@KotlinBuilder
data class EmployerDto(
        override val name: String? = null,
        val addresse: AddressDto? = null
) : NamedDto, Serializable

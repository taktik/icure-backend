package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable

@KotlinBuilder
data class AdministrationQuantity(
        val quantity: Double? = null,
        val administrationUnit: CodeStubDto? = null, //CD-ADMINISTRATIONUNIT
        val unit: String? = null //Should be null
) : Serializable

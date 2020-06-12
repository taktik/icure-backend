package org.taktik.icure.services.external.rest.v1.dto.samv2.stub

import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifiableDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SamTextDto

@KotlinBuilder
data class VmpGroupStubDto(
        override val id: String,
        val code: String? = null,
        val name: SamTextDto? = null
) : IdentifiableDto<String>

package org.taktik.icure.services.external.rest.v1.dto.samv2.stub

import org.taktik.icure.services.external.rest.v1.dto.base.IdentifiableDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SamTextDto

import com.github.pozo.KotlinBuilder
@KotlinBuilder
import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class VmpStubDto(
        override val id: String,
        val code: String? = null,
        val vmpGroup: VmpGroupStubDto? = null,
        val name: SamTextDto? = null
) : IdentifiableDto<String>

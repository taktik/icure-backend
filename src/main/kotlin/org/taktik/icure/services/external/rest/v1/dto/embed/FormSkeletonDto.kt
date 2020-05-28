package org.taktik.icure.services.external.rest.v1.dto.embed


import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class FormSkeletonDto(
        val descr: String? = null,
        val formTemplateId: String? = null
)

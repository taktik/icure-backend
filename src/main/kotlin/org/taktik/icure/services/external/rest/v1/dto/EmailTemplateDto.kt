package org.taktik.icure.services.external.rest.v1.dto

import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class EmailTemplateDto(val subject: String? = null, val body: String? = null)

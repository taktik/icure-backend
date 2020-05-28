package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

import com.github.pozo.KotlinBuilder
@KotlinBuilder
import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class CommentedClassificationDto(val title: SamTextDto? = null, val url: SamTextDto? = null, val commentedClassification: List<CommentedClassificationDto>? = null) : Serializable

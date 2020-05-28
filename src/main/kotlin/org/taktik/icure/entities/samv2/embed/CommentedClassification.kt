package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class CommentedClassification(val title: SamText? = null, val url: SamText? = null, val commentedClassification: List<CommentedClassification>? = null) : Serializable

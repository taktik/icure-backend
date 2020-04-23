package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class CommentedClassification(val title: SamText? = null, val url: SamText? = null, val commentedClassification: List<CommentedClassification>? = null) : Serializable

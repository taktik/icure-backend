package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class CommentedClassificationDto(var title: SamTextDto? = null, var url: SamTextDto? = null, var commentedClassification: List<CommentedClassificationDto>? = null) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentedClassificationDto

        if (title != other.title) return false
        if (url != other.url) return false
        if (commentedClassification != other.commentedClassification) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (commentedClassification?.hashCode() ?: 0)
        return result
    }
}

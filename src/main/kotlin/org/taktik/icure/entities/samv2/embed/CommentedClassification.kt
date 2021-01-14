package org.taktik.icure.entities.samv2.embed

import java.io.Serializable
import java.util.*

class CommentedClassification(var title: SamText? = null, var url: SamText? = null, var commentedClassification: SortedSet<CommentedClassification>? = null) : Serializable, Comparable<CommentedClassification> {
    override fun compareTo(other: CommentedClassification): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.title }, { it.hashCode() }).also { if(it==0) throw IllegalStateException("Invalid compareTo implementation") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentedClassification

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

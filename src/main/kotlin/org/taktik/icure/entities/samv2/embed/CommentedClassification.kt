package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class CommentedClassification(title: SamText?, url: SamText?, commentedClassification: List<CommentedClassification>) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

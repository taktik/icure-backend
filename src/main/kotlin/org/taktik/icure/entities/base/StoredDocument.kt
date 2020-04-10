/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.squareup.moshi.Json
import org.ektorp.Attachment
import org.ektorp.util.Assert
import org.taktik.icure.entities.embed.RevisionInfo
import java.io.Serializable
import java.util.Comparator
import java.util.Objects
import java.util.TreeMap

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class StoredDocument(
        @property:Json(name = "_id") override val id: String,
        @property:Json(name = "_rev") override val rev: String? = null,
        @property:Json(name = "_revs_info") val revisionsInfo: Array<RevisionInfo> = arrayOf(),
        @property:Json(name = "_conflicts") var conflicts: Array<String> = arrayOf(),
        @property:Json(name = "rev_history") override val revHistory: Map<String, String> = mapOf()
) : Versionable<String> {
    @Json(name = "java_type")
    val _type: String = this.javaClass.name

    @Json(name = "_attachments")
    var attachments : Map<String, Attachment> = mapOf()

    @Json(name = "deleted")
    var deletionDate: Long? = null

    @JsonIgnore
    fun addInlineAttachment(a: Attachment) {
        Assert.notNull(a, "attachment may not be null")
        Assert.hasText(a.dataBase64, "attachment must have data base64-encoded")
        attachments = attachments + (a.id to a)
    }

    fun delete() {
        deletionDate = System.currentTimeMillis()
    }

    @JsonIgnore
    fun deleteInlineAttachment(id: String) {
        Assert.notNull(id, "id may not be null")
        attachments = attachments - id
    }

    private fun reversedTreeMap(): TreeMap<String, String> {
        return TreeMap(ReversedSortedMapComparator())
    }

    private class ReversedSortedMapComparator : Comparator<String>, Serializable {
        override fun compare(o1: String, o2: String): Int {
            return o2.compareTo(o1)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StoredDocument) return false
        val that = other
        return deletionDate == that.deletionDate &&
                id == that.id &&
                rev == that.rev
    }

    override fun hashCode(): Int {
        return Objects.hash(deletionDate, id)
    }

    protected fun solveConflictsWith(other: StoredDocument?) {}
}

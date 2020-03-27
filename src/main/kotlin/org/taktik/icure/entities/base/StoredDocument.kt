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
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import org.ektorp.Attachment
import org.ektorp.util.Assert
import org.taktik.icure.entities.embed.RevisionInfo
import java.io.Serializable
import java.util.Comparator
import java.util.HashMap
import java.util.Objects
import java.util.TreeMap

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class StoredDocument : Versionable<String?> {
    constructor() {}
    constructor(id: String?) {
        this.id = id
    }

    @JsonProperty("_attachments")
    @Json(name = "_attachments")
    private var attachments: MutableMap<String, Attachment>? = HashMap()

    //Do not use deleted as a field... because it is translated to _deleted by ektorp :-(
    @JsonProperty("deleted")
    @Json(name = "deleted")
    var deletionDate: Long? = null

    @get:Json(name = "_id")
    @get:JsonProperty("_id")
    @set:Json(name = "_id")
    @set:JsonProperty("_id")
    @JsonProperty("_id")
    @Json(name = "_id")
    override var id: String? = null

    @get:Json(name = "_rev")
    @get:JsonProperty("_rev")
    @set:Json(name = "_rev")
    @set:JsonProperty("_rev")
    @JsonProperty("_rev")
    @Json(name = "_rev")
    override var rev: String? = null

    @get:Json(name = "_revs_info")
    @get:JsonProperty("_revs_info")
    @set:JsonProperty("_revs_info")
    @set:Json(name = "_revs_info")
    var revisionsInfo: Array<RevisionInfo>? = null

    @get:Json(name = "_conflicts")
    @get:JsonProperty("_conflicts")
    @set:JsonProperty("_conflicts")
    @set:Json(name = "_conflicts")
    var conflicts: Array<String>? = null
        protected set

    @JsonProperty("java_type")
    @Json(name = "java_type")
    protected var _type = this.javaClass.name

    @JsonProperty("rev_history")
    @Json(name = "rev_history")
    @set:JsonProperty("rev_history")
    @set:Json(name = "rev_history")
    override var revHistory: Map<String, String>? = reversedTreeMap()
    @JsonProperty("rev_history")
    @Json(name = "rev_history")
    get() {
        return if (field == null) reversedTreeMap() else field
    }

    @JsonIgnore
    fun addInlineAttachment(a: Attachment) {
        Assert.notNull(a, "attachment may not be null")
        Assert.hasText(a.dataBase64, "attachment must have data base64-encoded")
        if (attachments == null) {
            attachments = HashMap()
        }
        attachments!![a.id] = a
    }

    fun delete() {
        deletionDate = System.currentTimeMillis()
    }

    @JsonIgnore
    fun deleteInlineAttachment(id: String) {
        Assert.notNull(id, "id may not be null")
        if (attachments != null) {
            attachments!!.remove(id)
        }
    }

    fun getAttachments(): Map<String, Attachment>? {
        return attachments
    }

    fun setAttachments(attachments: MutableMap<String, Attachment>?) {
        this.attachments = attachments
    }

    private fun reversedTreeMap(): TreeMap<String, String> {
        return TreeMap(ReversedSortedMapComparator())
    }

    private class ReversedSortedMapComparator : Comparator<String>, Serializable {
        override fun compare(o1: String, o2: String): Int {
            return o2.compareTo(o1)
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is StoredDocument) return false
        val that = o
        return deletionDate == that.deletionDate &&
                id == that.id &&
                rev == that.rev
    }

    override fun hashCode(): Int {
        return Objects.hash(deletionDate, id)
    }

    protected fun solveConflictsWith(other: StoredDocument?) {}

    companion object {
        private const val serialVersionUID = 1L
        fun <T : StoredDocument?> strip(document: T): T {
            val newDoc: T
            try {
                newDoc = document!!.javaClass.newInstance() as T
                newDoc!!.id = document.id
                newDoc.rev = document.rev
            } catch (e: InstantiationException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }
            return newDoc
        }
    }
}

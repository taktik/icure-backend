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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.TypedValue
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Property(id: String,
               rev: String? = null,
               revisionsInfo: Array<RevisionInfo> = arrayOf(),
               conflicts: Array<String> = arrayOf(),
               revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Identifiable<String>, Cloneable, Serializable {
    var type: PropertyType? = null
    var typedValue: TypedValue<*>? = null

    @JsonIgnore
    fun <T> getValue(): T? {
        return (if (typedValue != null) typedValue!!.getValue<Any>() else null) as T?
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (type == null) 0 else type.hashCode()
        result = prime * result + if (typedValue == null) 0 else typedValue.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Property
        if (type == null) {
            if (other.type != null) return false
        } else if (type != other.type) return false
        if (typedValue == null) {
            if (other.typedValue != null) return false
        } else if (!typedValue!!.equals(other.typedValue)) return false
        return true
    }

}

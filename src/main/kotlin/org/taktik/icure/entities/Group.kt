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
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.RevisionInfo
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Group(id: String,
            rev: String? = null,
            revisionsInfo: Array<RevisionInfo> = arrayOf(),
            conflicts: Array<String> = arrayOf(),
            revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Cloneable, Serializable {
    var name: String? = null
    var password: String? = null
    var servers: List<String>? = null
    var isSuperAdmin = false
    var superGroup: String? = null

    @JsonIgnore
    fun dbInstanceUrl(): String? {
        return if (servers != null && servers!!.size > 0) servers!![0] else null
    }
}

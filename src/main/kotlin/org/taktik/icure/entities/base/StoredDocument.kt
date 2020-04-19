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

import com.squareup.moshi.Json
import org.ektorp.Attachment
import org.taktik.icure.entities.embed.RevisionInfo

interface StoredDocument : Versionable<String> {
    @Suppress("PropertyName")
    @Json(name = "java_type")
    val _type: String
    @Json(name = "_revs_info")
    val revisionsInfo: List<RevisionInfo>
    @Json(name = "_conflicts")
    val conflicts: List<String>
    @Json(name = "_attachments")
    val attachments : Map<String, Attachment>
    @Json(name = "deleted")
    val deletionDate: Long?

    fun solveConflictsWith(other: StoredDocument) : Map<String, Any?> {
        return mapOf(
                "id" to this.id,
                "rev" to this.rev,
                "_type" to this._type,
                "revHistory" to other.revHistory + this.revHistory,
                "revisionsInfo" to this.revisionsInfo,
                "conflicts" to this.conflicts,
                "attachments" to this.attachments,
                "deletionDate" to (this.deletionDate ?: other.deletionDate)
        )
    }
}

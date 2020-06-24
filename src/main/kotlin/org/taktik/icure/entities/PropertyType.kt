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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.constants.PropertyTypeScope
import org.taktik.icure.constants.TypedValuesType
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PropertyType(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        val identifier: String? = null,
        val type: TypedValuesType? = null,
        val scope: PropertyTypeScope? = null,
        val unique: Boolean = false,
        val editor: String? = null,
        val localized: Boolean = false,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null

) : StoredDocument {
    companion object : DynamicInitializer<PropertyType> {
        fun with(type: TypedValuesType, scope: PropertyTypeScope, identifier: String) = PropertyType(id = identifier, type = type, scope = scope, identifier = identifier)
    }

    fun merge(other: PropertyType) = PropertyType(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: PropertyType) = super.solveConflictsWith(other) + mapOf(
            "identifier" to (this.identifier ?: other.identifier),
            "type" to (this.type ?: other.type),
            "scope" to (this.scope ?: other.scope),
            "unique" to (this.unique),
            "editor" to (this.editor ?: other.editor),
            "localized" to (this.localized)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

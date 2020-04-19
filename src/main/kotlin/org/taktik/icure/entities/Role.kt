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
import org.ektorp.Attachment
import org.taktik.icure.entities.base.Principal
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Role(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String?,
        @JsonProperty("deleted") override val deletionDate: Long?,

        override val name: String? = null,
        override val properties: Set<Property> = setOf(),
        override val permissions: Set<Permission> = setOf(),
        val children: Set<String> = setOf(),
        val users: Set<String> = setOf(),

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>,
        @JsonProperty("_conflicts") override val conflicts: List<String>,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>,
        @JsonProperty("java_type") override val _type: String = Role::javaClass.name
) : StoredDocument, Principal, Cloneable, Serializable {
    companion object : DynamicInitializer<Role>
    fun merge(other: Role) = Role(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Role) = super.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "properties" to (other.properties + this.properties),
            "permissions" to (other.permissions + this.permissions),
            "children" to (other.children + this.children),
            "users" to (other.users + this.users)
    )

}

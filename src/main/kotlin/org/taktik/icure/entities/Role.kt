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
import org.taktik.icure.constants.Roles.VirtualHostDependency
import org.taktik.icure.entities.base.Principal
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.entities.embed.RevisionInfo
import java.io.Serializable
import java.util.HashSet

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Role(id: String,
           rev: String? = null,
           revisionsInfo: Array<RevisionInfo> = arrayOf(),
           conflicts: Array<String> = arrayOf(),
           revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Principal, Cloneable, Serializable {
    override var name: String? = null
    override var properties: MutableSet<Property> = HashSet()
    override var permissions: MutableSet<Permission> = HashSet()
    var children: Set<String> = HashSet()
    var users: Set<String> = HashSet()

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val role = other as Role
        return id == role.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

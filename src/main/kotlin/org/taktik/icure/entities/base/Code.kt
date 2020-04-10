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
import com.google.common.collect.ImmutableMap
import org.taktik.icure.entities.embed.Periodicity
import org.taktik.icure.entities.embed.RevisionInfo
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class Code(id: String,
                rev: String? = null,
                revisionsInfo: Array<RevisionInfo> = arrayOf(),
                conflicts: Array<String> = arrayOf(),
                revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), CodeIdentification {
    // id = type|code|version  => this must be unique
    var author: String? = null
    var regions //ex: be,fr
            : Set<String>? = null
    var periodicity: List<Periodicity>? = null
    override var type //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
            : String? = null
    set(type) {
        id = "$type|$code|$version"
        field = type
    }
    override var code //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
            : String? = null
    set(code) {
        id = "$type|$code|$version"
        field = code
    }
    override var version //ex: 10. Must be lexicographically searchable
            : String? = null
        set(version) {
            id = "$type|$code|$version"
            field = version
        }

    var level //ex: 0 = System, not to be modified by user, 1 = optional, created or modified by user
            : Int? = null
    var label //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
            : MutableMap<String, String>? = null

    @Deprecated("Use qualified links instead")
    var links //Links towards related codes (corresponds to an approximate link in qualifiedLinks)
            : List<String>? = null
    var qualifiedLinks //Links towards related codes
            : Map<LinkQualification, List<String>>? = null
    var flags //flags (like female only) for the code
            : Set<CodeFlag>? = null
    var searchTerms //Extra search terms/ language
            : Map<String, Set<String>>? = null
    protected var data: String? = null
    var appendices: Map<AppendixType, String>? = null
    var isDisabled = false

    override fun toString(): String {
        return "$type:$code"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Code) return false
        if (!super.equals(other)) return false
        val code1 = other
        return type == code1.type &&
                code == code1.code &&
                version == code1.version
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), type, code, version)
    }

    companion object {
        private const val serialVersionUID = 1L
        val versionsMap: Map<String, String> = ImmutableMap.of(
                "INAMI-RIZIV", "1.0"
        )

        fun dataCode(typeAndCodeAndVersion: String, data: String?): Code {
            val c = Code(typeAndCodeAndVersion)
            c.data = data
            return c
        }
    }
}

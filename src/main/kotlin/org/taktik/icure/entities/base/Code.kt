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
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class Code : StoredDocument, CodeIdentification {
    // id = type|code|version  => this must be unique
    var author: String? = null
    var regions //ex: be,fr
            : Set<String>? = null
    var periodicity: List<Periodicity>? = null
    protected var type //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
            : String? = null
    protected var code //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
            : String? = null
    protected var version //ex: 10. Must be lexicographically searchable
            : String? = null
    var level //ex: 0 = System, not to be modified by user, 1 = optional, created or modified by user
            : Int? = null
    protected var label //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
            : MutableMap<String?, String?>? = null

    @Deprecated("")
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

    constructor() {}
    constructor(typeAndCodeAndVersion: String) : this(typeAndCodeAndVersion.split("\\|".toRegex()).toTypedArray()[0], typeAndCodeAndVersion.split("\\|".toRegex()).toTypedArray()[1], typeAndCodeAndVersion.split("\\|".toRegex()).toTypedArray()[2]) {}
    constructor(type: String, code: String, version: String) : this(HashSet<String>(Arrays.asList<String>("be", "fr")), type, code, version) {}
    constructor(region: String, type: String?, code: String?, version: String?) : this(setOf<String>(region), type!!, code!!, version!!) {}

    @JvmOverloads
    constructor(regions: Set<String>?, type: String, code: String, version: String, label: MutableMap<String?, String?>? = HashMap()) {
        this.regions = regions
        this.type = type
        this.code = code
        this.version = version
        this.label = label
        id = "$type|$code|$version"
    }

    override fun toString(): String {
        return type + ":" + code
    }

    @get:JsonIgnore
    @get:Deprecated("")
    @set:Deprecated("")
    var descrFR: String?
        get() = if (label != null) label!!["fr"] else null
        set(descrFR) {
            if (label == null) {
                label = HashMap()
            }
            label!!["fr"] = descrFR
        }

    @get:JsonIgnore
    @get:Deprecated("")
    @set:Deprecated("")
    var descrNL: String?
        get() = if (label != null) label!!["nl"] else null
        set(descrNL) {
            if (label == null) {
                label = HashMap()
            }
            label!!["nl"] = descrNL
        }

    override fun getCode(): String? {
        return code
    }

    override fun setCode(code: String) {
        id = "$type|$code|$version"
        this.code = code
    }

    fun getLabel(): Map<String?, String?>? {
        return label
    }

    fun setLabel(label: MutableMap<String?, String?>?) {
        this.label = label
    }

    override fun getType(): String? {
        return type
    }

    override fun setType(type: String) {
        id = "$type|$code|$version"
        this.type = type
    }

    override fun getVersion(): String? {
        return version
    }

    override fun setVersion(version: String) {
        id = "$type|$code|$version"
        this.version = version
    }

    fun getData(): String? {
        return data
    }

    fun setData(data: String?) {
        this.data = data
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Code) return false
        if (!super.equals(o)) return false
        val code1 = o
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

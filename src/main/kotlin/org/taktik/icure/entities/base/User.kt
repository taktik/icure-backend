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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import java.util.ArrayList

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class User : Versionable<String?> {
    @JsonProperty("_id")
    @Json(name = "_id")
    override var id: String? = null

    @JsonProperty("_rev")
    @Json(name = "_rev")
    override var rev: String? = null
    var name: String? = null
    var password: String? = null
    var type = "user"
    var roles: List<String> = ArrayList()

    constructor() {}
    constructor(id: String?, name: String?, password: String?) {
        this.id = id
        this.name = name
        this.password = password
    }

    override val revHistory: Map<String, String>?
        get() = null

    override fun getId(): String? {
        return null
    }

    override fun setId(id: String?) {}
}

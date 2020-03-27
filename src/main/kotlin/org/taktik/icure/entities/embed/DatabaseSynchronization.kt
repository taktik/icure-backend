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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.util.Objects

@JsonIgnoreProperties(ignoreUnknown = true)
class DatabaseSynchronization : Serializable {
    var source: String? = null
        protected set
    var target: String? = null
        protected set
    var filter: String? = null
        protected set

    constructor() {}

    @JvmOverloads
    constructor(source: String?, target: String?, filter: String? = null) {
        this.source = source
        this.target = target
        this.filter = filter
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as DatabaseSynchronization
        return source == that.source &&
                target == that.target
    }

    override fun hashCode(): Int {
        return Objects.hash(source, target)
    }
}

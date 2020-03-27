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
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable
import java.util.Objects

/**
 * Created by aduchate on 29/03/13, 18:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Delegation : Serializable {
    //This is in no way the owner of a piece of date (patient, contact). It is the owner of the delegation.
    var owner // owner id
            : String? = null
    var delegatedTo // delegatedTo id
            : String? = null
    var key // An arbitrary key (generated, patientId, any ID, etc.), usually prefixed with the entity ID followed by ":", encrypted using an exchange AES key.
            : String? = null
    var tags // Used for rights
            : List<String>? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Delegation
        return owner == that.owner &&
                delegatedTo == that.delegatedTo &&
                key == that.key &&
                tags == that.tags
    }

    override fun hashCode(): Int {
        return Objects.hash(owner, delegatedTo, key, tags)
    }
}

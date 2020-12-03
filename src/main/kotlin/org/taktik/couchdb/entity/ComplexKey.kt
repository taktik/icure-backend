/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.couchdb.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taktik.couchdb.handlers.JacksonComplexKeyDeserializer
import org.taktik.couchdb.handlers.JacksonComplexKeySerializer
import java.util.*

/**
 * Class for creating complex keys for view queries.
 * The keys's components can consists of any JSON-encodeable objects, but are most likely to be Strings and Integers.
 * @author henrik lundgren
 */
@JsonDeserialize(using = JacksonComplexKeyDeserializer::class)
@JsonSerialize(using = JacksonComplexKeySerializer::class)
class ComplexKey(components: Array<Any?> = arrayOf()) {
    val components: List<Any?> = listOf(*components)

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ComplexKey
        return components == that.components
    }

    override fun hashCode(): Int {
        return Objects.hash(components)
    }

    companion object {
        private val EMPTY_OBJECT = Any()
        private val EMPTY_ARRAY = arrayOf<Any>()

        fun of(vararg components: Any?): ComplexKey {
            return ComplexKey(arrayOf(*components))
        }

        /**
         * Add this Object to the key if an empty object definition is desired:
         * ["foo",{}]
         * @return an object that will serialize to {}
         */
        fun emptyObject(): Any {
            return EMPTY_OBJECT
        }

        /**
         * Add this array to the key if an empty array definition is desired:
         * [[],"foo"]
         * @return an object array that will serialize to []
         */
        fun emptyArray(): Array<Any> {
            return EMPTY_ARRAY
        }
    }

}

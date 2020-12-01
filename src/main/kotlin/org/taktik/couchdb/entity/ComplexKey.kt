package org.taktik.couchdb.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taktik.icure.handlers.JacksonComplexKeyDeserializer
import org.taktik.icure.handlers.JacksonComplexKeySerializer
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

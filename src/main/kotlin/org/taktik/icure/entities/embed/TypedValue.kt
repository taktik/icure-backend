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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taktik.icure.constants.TypedValuesType
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import java.io.Serializable
import java.time.Instant
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class TypedValue<T>  : Comparable<TypedValue<T>>, Cloneable, Serializable {
    var type: TypedValuesType? = null
    var booleanValue: Boolean? = null
    var integerValue: Int? = null
    var doubleValue: Double? = null
    var stringValue: String? = null

    @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer::class)
    var dateValue: Instant? = null

    constructor() {}
    constructor(value: T) {
        setValue(value)
    }

    constructor(type: TypedValuesType?, value: T?) {
        setTypeAndValue(type, value)

        // Check value has been set
        require(!(value != null && getValue<Any?>() == null)) { "value type incompatible with typedvalue type !" }
    }

    @JsonIgnore
    fun <T> getValue(): T? {
        if (type == null) {
            return null
        }
        when (type) {
            TypedValuesType.BOOLEAN -> return booleanValue as T?
            TypedValuesType.INTEGER -> return integerValue as T?
            TypedValuesType.DOUBLE -> return doubleValue as T?
            TypedValuesType.STRING, TypedValuesType.CLOB, TypedValuesType.JSON -> return stringValue as T?
            TypedValuesType.DATE -> return dateValue as T?
        }
        return null
    }

    @JsonIgnore
    private fun <T> setValue(value: T) {
        // Auto-detect type
        var type: TypedValuesType? = null
        if (value is Boolean) {
            type = TypedValuesType.BOOLEAN
        } else if (value is Int) {
            type = TypedValuesType.INTEGER
        } else if (value is Double) {
            type = TypedValuesType.DOUBLE
        } else if (value is String) {
            type = TypedValuesType.STRING
        } else if (value is Date) {
            type = TypedValuesType.DATE
        }

        // Set type and value
        setTypeAndValue(type, value)
    }

    private fun <T> setTypeAndValue(type: TypedValuesType?, value: T?) {
        // Set type
        this.type = type

        // Reset value to null
        booleanValue = null
        integerValue = null
        doubleValue = null
        stringValue = null
        dateValue = null

        // Set value if it matches the chosen type
        if (type != null && value != null) {
            when (type) {
                TypedValuesType.BOOLEAN -> if (value is Boolean) {
                    booleanValue = value
                }
                TypedValuesType.INTEGER -> if (value is Int) {
                    integerValue = value
                }
                TypedValuesType.DOUBLE -> if (value is Double) {
                    doubleValue = value
                }
                TypedValuesType.STRING, TypedValuesType.JSON, TypedValuesType.CLOB -> if (value is String) {
                    stringValue = value
                }
                TypedValuesType.DATE -> if (value is Instant) {
                    dateValue = value
                } else if (value is Date) {
                    dateValue = (value as Date).toInstant()
                }
            }
        }
    }

    override fun compareTo(other: TypedValue<T>): Int {
        return (other.getValue<T>() as Comparable<T>).compareTo(getValue<T>()!!)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (type == null) 0 else type.hashCode()
        result = prime * result + if (booleanValue == null) 0 else booleanValue.hashCode()
        result = prime * result + if (integerValue == null) 0 else integerValue.hashCode()
        result = prime * result + if (doubleValue == null) 0 else doubleValue.hashCode()
        result = prime * result + if (stringValue == null) 0 else stringValue.hashCode()
        result = prime * result + if (dateValue == null) 0 else dateValue.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (javaClass != obj!!.javaClass) return false
        val other = obj as TypedValue<T>?
        if (booleanValue == null) {
            if (other!!.booleanValue != null) return false
        } else if (booleanValue != other!!.booleanValue) return false
        if (dateValue == null) {
            if (other.dateValue != null) return false
        } else if (dateValue != other.dateValue) return false
        if (doubleValue == null) {
            if (other.doubleValue != null) return false
        } else if (doubleValue != other.doubleValue) return false
        if (integerValue == null) {
            if (other.integerValue != null) return false
        } else if (integerValue != other.integerValue) return false
        if (stringValue == null) {
            if (other.stringValue != null) return false
        } else if (stringValue != other.stringValue) return false
        return if (type != other.type) false else true
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    override fun toString(): String {
        if (type != null) {
            when (type) {
                TypedValuesType.BOOLEAN -> return booleanValue.toString()
                TypedValuesType.INTEGER -> return integerValue.toString()
                TypedValuesType.DOUBLE -> return doubleValue.toString()
                TypedValuesType.STRING, TypedValuesType.CLOB, TypedValuesType.JSON -> return stringValue!!
                TypedValuesType.DATE -> return dateValue.toString()
            }
        }
        return super.toString()
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

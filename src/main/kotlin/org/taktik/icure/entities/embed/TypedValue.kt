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
package org.taktik.icure.entities.embed

import java.io.Serializable
import java.time.Instant
import java.util.*
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.constants.TypedValuesType
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TypedValue<T>(
	val type: TypedValuesType? = null,
	val booleanValue: Boolean? = null,
	val integerValue: Int? = null,
	val doubleValue: Double? = null,
	val stringValue: String? = null,

	@JsonSerialize(using = InstantSerializer::class)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonDeserialize(using = InstantDeserializer::class)
	val dateValue: Instant? = null,
	override val encryptedSelf: String? = null
) : Comparable<TypedValue<T>>, Encrypted, Serializable {
	companion object {
		fun <T> withValue(value: T?): TypedValue<T>? = value?.let {
			withTypeAndValue(
				when (value) {
					is Boolean -> TypedValuesType.BOOLEAN
					is Int -> TypedValuesType.INTEGER
					is Double -> TypedValuesType.DOUBLE
					is String -> TypedValuesType.STRING
					is Date -> TypedValuesType.DATE
					else -> throw IllegalArgumentException("Unknown value type")
				},
				value
			)
		}

		fun <T> withTypeAndValue(type: TypedValuesType, value: T?): TypedValue<T>? = value?.let {
			when (type) {
				TypedValuesType.BOOLEAN -> if (value is Boolean) {
					TypedValue(booleanValue = value, type = type)
				} else throw IllegalArgumentException("Illegal boolean value")
				TypedValuesType.INTEGER -> if (value is Int) {
					TypedValue(integerValue = value, type = type)
				} else throw IllegalArgumentException("Illegal integer value")
				TypedValuesType.DOUBLE -> if (value is Double) {
					TypedValue(doubleValue = value, type = type)
				} else throw IllegalArgumentException("Illegal double value")
				TypedValuesType.STRING, TypedValuesType.JSON, TypedValuesType.CLOB -> if (value is String) {
					TypedValue(stringValue = value, type = type)
				} else throw IllegalArgumentException("Illegal string value")
				TypedValuesType.DATE -> if (value is Instant) {
					TypedValue(dateValue = value, type = type)
				} else if (value is Date) {
					TypedValue(dateValue = (value as Date).toInstant(), type = type)
				} else throw IllegalArgumentException("Illegal date value")
			}
		}
	}

	@JsonIgnore
	fun <T> getValue(): T? {
		if (type == null) {
			return null
		}
		return when (type) {
			TypedValuesType.BOOLEAN -> booleanValue as? T
			TypedValuesType.INTEGER -> integerValue as? T
			TypedValuesType.DOUBLE -> doubleValue as? T
			TypedValuesType.STRING, TypedValuesType.CLOB, TypedValuesType.JSON -> stringValue as? T
			TypedValuesType.DATE -> dateValue as? T
		}
	}

	override fun compareTo(other: TypedValue<T>): Int {
		return (other.getValue<T>() as Comparable<T>).compareTo(getValue<T>()!!)
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
}

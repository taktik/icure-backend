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
package org.taktik.icure.constants

enum class TypedValuesType {
    BOOLEAN, INTEGER, DOUBLE, STRING, DATE, CLOB, JSON;

    companion object {
        fun fromInt(value: Int): TypedValuesType {
            return TypedValuesType::class.java.enumConstants[value]
        }

        fun fetchAll(): String {
            val all = StringBuilder()
            for (type in values()) {
                all.append(type)
                all.append("\n")
            }
            return all.toString()
        }
    }
}

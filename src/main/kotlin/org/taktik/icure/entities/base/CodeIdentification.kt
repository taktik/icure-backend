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

package org.taktik.icure.entities.base

interface CodeIdentification {
    val id: String
    val code: String?
    val type: String?
    val version: String?
    val context: String?
    val label: Map<String, String>

    fun solveConflictsWith(other: CodeIdentification): Map<String, Any?> {
        return mapOf(
                "id" to (this.id),
                "code" to (this.code ?: other.code),
                "type" to (this.type ?: other.type),
                "context" to (this.context ?: other.context),
                "version" to (this.version ?: other.version),
                "label" to (other.label + this.label)
        )
    }
    fun normalizeIdentification() : CodeIdentification
}

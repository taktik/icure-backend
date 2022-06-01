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

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.handlers.CodeStubDeserializer
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = CodeStubDeserializer::class)
@KotlinBuilder
data class CodeStub(
	@JsonProperty("_id") override val id: String, // id = type|code|version  => this must be unique
	override val context: String? = null, //ex: When embedded the context where this code is used
	override val type: String? = null, //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
	override val code: String? = null, //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
	override val version: String? = null, //ex: 10. Must be lexicographically searchable
	override val label: Map<String, String>? = null //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
) : CodeIdentification, Serializable {

	companion object : DynamicInitializer<CodeStub> {
		fun from(type: String, code: String, version: String) = CodeStub(id = "$type|$code|$version", type = type, code = code, version = version)
	}

	fun merge(other: CodeStub) = CodeStub(args = this.solveConflictsWith(other))
	fun solveConflictsWith(other: CodeStub) = super.solveConflictsWith(other)

	override fun normalizeIdentification(): CodeStub {
		val parts = this.id.split("|").toTypedArray()
		return if (this.type == null || this.code == null || this.version == null) this.copy(
			type = this.type ?: parts[0],
			code = this.code ?: parts[1],
			version = this.version ?: parts[2]
		) else this
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CodeStub) return false

		if (id != other.id) return false
		if (context != other.context) return false
		if (type != other.type) return false
		if (code != other.code) return false
		if (version != other.version) return false
		if (label != other.label && ((label?.size ?: 0) > 0 || ((other.label?.size ?: 0) > 0))) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + (context?.hashCode() ?: 0)
		result = 31 * result + (type?.hashCode() ?: 0)
		result = 31 * result + (code?.hashCode() ?: 0)
		result = 31 * result + (version?.hashCode() ?: 0)
		return result
	}
}

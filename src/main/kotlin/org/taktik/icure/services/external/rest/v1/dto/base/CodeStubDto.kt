package org.taktik.icure.services.external.rest.v1.dto.base

import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke


data class CodeStubDto(
        override val id: String,         // id = type|code|version  => this must be unique
        override val type: String? = null, //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
        override val code: String? = null, //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
        override val version: String? = null //ex: 10. Must be lexicographically searchable
) : CodeIdentificationDto {
    companion object : DynamicInitializer<CodeStubDto>

    fun merge(other: CodeStubDto) = CodeStubDto(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: CodeStubDto) = super.solveConflictsWith(other)

    override fun normalizeIdentification(): CodeStubDto {
        val parts = this.id.split("|").toTypedArray()
        return if (this.type == null || this.code == null || this.version == null) this.copy(
                type = this.type ?: parts[0],
                code = this.code ?: parts[1],
                version = this.version ?: parts[2]
        ) else this
    }

}

package org.taktik.icure.services.external.rest.v1.dto.base

interface CodeIdentificationDto {
    val id: String
    val code: String?
    val type: String?
    val version: String?

    fun solveConflictsWith(other: CodeIdentificationDto): Map<String, Any?> {
        return mapOf(
                "id" to (this.id),
                "code" to (this.code ?: other.code),
                "type" to (this.type ?: other.type),
                "version" to (this.version ?: other.version)
        )
    }

    fun normalizeIdentification(): CodeIdentificationDto
}

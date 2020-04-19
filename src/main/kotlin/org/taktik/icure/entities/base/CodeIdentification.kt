package org.taktik.icure.entities.base

interface CodeIdentification {
    val id: String
    val code: String?
    val type: String?
    val version: String?

    fun solveConflictsWith(other: CodeIdentification) : Map<String, Any?> {
        return mapOf(
            "id" to (this.id),
            "code" to (this.code ?: other.code),
            "type" to (this.type ?: other.type),
            "version" to (this.version ?: other.version)
        )
    }
}

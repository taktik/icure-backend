package org.taktik.icure.services.external.rest.v1.dto.embed

interface EncryptedDto {
    val encryptedSelf: String?

    fun solveConflictsWith(other: EncryptedDto) = mapOf(
            "encryptedSelf" to (this.encryptedSelf ?: other.encryptedSelf)
    )
}

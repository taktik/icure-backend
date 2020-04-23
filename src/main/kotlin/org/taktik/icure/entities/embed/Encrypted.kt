package org.taktik.icure.entities.embed

interface Encrypted {
    val encryptedSelf: String?

    fun solveConflictsWith(other: Encrypted) = mapOf(
            "encryptedSelf" to (this.encryptedSelf ?: other.encryptedSelf)
    )
}

package org.taktik.icure.entities.base

interface StoredICureDocument : StoredDocument, ICureDocument<String> {
    fun solveConflictsWith(other: StoredICureDocument) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other)
}

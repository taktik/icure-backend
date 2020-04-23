package org.taktik.icure.entities.base

import org.taktik.icure.entities.AccessLog

interface StoredICureDocument : StoredDocument, ICureDocument {
    fun solveConflictsWith(other: StoredICureDocument) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other)
}

package org.taktik.couchdb.exception

/**
 *
 * @author Henrik Lundgren
 * created 18 okt 2009
 */
class UpdateConflictException : DbAccessException {
    private val docId: String
    private val revision: String

    constructor(documentId: String, revision: String) {
        docId = documentId
        this.revision = revision
    }

    constructor() {
        docId = "unknown"
        revision = "unknown"
    }

    override val message: String
        get() = String.format("document update conflict: id: %s rev: %s", docId, revision)
}

package org.taktik.couchdb.exception

import com.fasterxml.jackson.databind.JsonNode

/**
 *
 * @author Henrik Lundgren
 * created 7 nov 2009
 */
class DocumentNotFoundException : DbAccessException {
    val path: String?
    val body: JsonNode?

    constructor(path: String?, responseBody: JsonNode?) : super(String.format("nothing found on db path: %s, Response body: %s", path, responseBody)) {
        this.path = path
        body = responseBody
    }

    constructor(path: String?) : super(String.format("nothing found on db path: %s", path)) {
        this.path = path
        body = null
    }

    private fun checkReason(expect: String): Boolean {
        if (body == null) {
            return false
        }
        val reason = body.findPath("reason")
        return if (!reason.isMissingNode) reason.textValue() == expect else false
    }

    val isDocumentDeleted: Boolean
        get() = checkReason("deleted")
    val isDatabaseDeleted: Boolean
        get() = checkReason("no_db_file")
}

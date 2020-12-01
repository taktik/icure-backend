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

/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.RevisionInfo

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class AccessLog(id: String,
                rev: String? = null,
                revisionsInfo: Array<RevisionInfo> = arrayOf(),
                conflicts: Array<String> = arrayOf(),
                revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory), Encryptable {

    @Deprecated("Use cryptedForeignKeys instead")
    var patientId: String? = null

    var accessType: String? = null
    var user: String? = null
    var detail: String? = null
    var objectId: String? = null

    companion object {
        val USER_ACCESS = "USER_ACCESS"
        val COMPUTER_ACCESS = "COMPUTER_ACCESS"
        val LOGIN_ACCESS = "LOGIN_ACCESS"
    }
}

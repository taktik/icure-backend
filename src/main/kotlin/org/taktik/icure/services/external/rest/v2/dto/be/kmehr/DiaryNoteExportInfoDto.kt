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

package org.taktik.icure.services.external.rest.v2.dto.be.kmehr

import org.taktik.icure.services.external.rest.v2.dto.HealthcarePartyDto
import java.io.Serializable

class DiaryNoteExportInfoDto : Serializable {
    var secretForeignKeys: List<String> = listOf()
    var excludedIds: List<String> = listOf()
    var recipient: HealthcarePartyDto? = null
    var softwareName: String? = null
    var softwareVersion: String? = null
    var tags: List<String> = listOf()
    var contexts: List<String> = listOf()
    var psy: Boolean? = null
    var documentId: String? = null
    var attachmentId: String? = null
    var note: String? = null
}

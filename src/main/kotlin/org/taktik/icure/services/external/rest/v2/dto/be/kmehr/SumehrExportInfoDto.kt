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

import java.io.Serializable
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v2.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v2.dto.embed.ServiceDto

class SumehrExportInfoDto : Serializable {
	var secretForeignKeys: List<String> = emptyList()
	var excludedIds: List<String> = emptyList()
	var recipient: HealthcarePartyDto? = null
	var softwareName: String? = null
	var softwareVersion: String? = null
	@Schema(defaultValue = "\"\"") var comment: String = ""
	var includeIrrelevantInformation: Boolean? = null
	var services: List<ServiceDto>? = null
	var healthElements: List<HealthElementDto>? = null
}

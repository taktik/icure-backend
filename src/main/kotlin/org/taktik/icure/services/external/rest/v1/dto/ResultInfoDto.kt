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
package org.taktik.icure.services.external.rest.v1.dto

import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto

class ResultInfoDto {
    var ssin: String? = null
    var lastName: String? = null
    var firstName: String? = null
    var dateOfBirth: Long? = null
    var sex: String? = null
    var documentId: String? = null
    var protocol: String? = null
    var complete: Boolean? = null
    var demandDate: Long? = null
    var labo: String? = null
    var engine: String? = null
    var codes: Set<CodeStubDto> = setOf()
    var services: List<ServiceDto> = listOf()
}

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

package org.taktik.icure.services.external.rest.v2.dto.be.efact

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

class ErrorDetail : Serializable {
    @Schema(defaultValue = "0") var creationDate: Int = 0
    var errorCodeComment: String? = null
    @Schema(defaultValue = "0") var index: Int = 0
    @Schema(defaultValue = "0") var invoicingYearMonth: Int = 0
    @Schema(defaultValue = "0") var mutualityCode: Int = 0
    var oaResult: String? = null
    var rejectionCode1: String? = null
    var rejectionCode2: String? = null
    var rejectionCode3: String? = null
    var rejectionLetter1: String? = null
    var rejectionLetter2: String? = null
    var rejectionLetter3: String? = null
    var rejectionDescr1: String? = null
    var rejectionDescr2: String? = null
    var rejectionDescr3: String? = null
    var rejectionZoneDescr1: String? = null
    var rejectionZoneDescr2: String? = null
    var rejectionZoneDescr3: String? = null
    var reserve: String? = null
    @Schema(defaultValue = "0") var sendingId: Int = 0
    var zone114: String? = null
    var zone115: String? = null
    var zone116: String? = null
    var zone117: String? = null
    var zone118: String? = null
}

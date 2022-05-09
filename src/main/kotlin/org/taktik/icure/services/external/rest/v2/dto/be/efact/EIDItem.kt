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

import java.util.*
import io.swagger.v3.oas.annotations.media.Schema

class EIDItem {
	var deviceType: String? = null
	var readDate: Long? = null
	@Schema(defaultValue = "0") var readHour: Int = 0
	var readType: String? = null
	var readvalue: String? = null

	constructor() {
		deviceType = "1"
		readType = "1"
		readDate = Date().time

		var cal = Calendar.getInstance()

		readHour = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE)
	}

	constructor(readDate: Long?, readHour: Int?, readvalue: String) {
		deviceType = "1"
		readType = "1"

		this.readvalue = readvalue
		this.readDate = readDate
		this.readHour = readHour!!
	}
}

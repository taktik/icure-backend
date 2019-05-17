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

package org.taktik.icure.services.external.rest.v1.dto.be.efact

import java.util.Calendar
import java.util.Date

class EIDItem {
    var deviceType: String? = null
    var readDate: Long? = null
    var readHour: Int = 0
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

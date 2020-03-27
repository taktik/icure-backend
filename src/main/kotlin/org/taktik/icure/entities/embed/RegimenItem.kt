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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.apache.commons.lang3.StringUtils
import org.taktik.icure.entities.base.Code
import java.io.Serializable
import java.text.SimpleDateFormat

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class RegimenItem : Serializable {
    //Day definition (One and only one of the three following should be not null)
    //The three are null if it applies to every day
    var date //yyyymmdd at this date
            : Long? = null
    var dayNumber //day 1 of treatment. 1 based numeration
            : Int? = null
    var weekday //on monday
            : Weekday? = null

    //Time of day definition (One and only one of the three following should be not null)
    //Both are null if not specified
    var dayPeriod //CD-DAYPERIOD
            : Code? = null
    var timeOfDay //hhmmss 103010
            : Long? = null
    var administratedQuantity: AdministrationQuantity? = null

    class Weekday : Serializable {
        var weekday //CD-WEEKDAY
                : Code? = null
        var weekNumber //Can be null
                : Int? = null

    }

    class AdministrationQuantity : Serializable {
        var quantity: Double? = null
        var administrationUnit //CD-ADMINISTRATIONUNIT
                : Code? = null
        var unit //Should be null
                : String? = null

        override fun toString(): String {
            return String.format("%f %s", quantity, if (administrationUnit != null) administrationUnit!!.getCode() else unit)
        }
    }

    override fun toString(): String {
        val df = SimpleDateFormat("dd/MM/yyyy")
        var result = if (date != null) String.format("the %s", df.format(date)) else if (dayNumber != null) String.format("on day %d", dayNumber) else if (weekday != null && weekday!!.weekday != null && weekday!!.weekday!!.getCode() != null) String.format("on %s", weekday!!.weekday!!.getCode()) else null
        if (dayPeriod != null && !StringUtils.isEmpty(dayPeriod!!.getCode())) {
            result = if (result != null) String.format("%s %s", result, dayPeriod!!.getCode()) else dayPeriod!!.getCode()
        }
        if (timeOfDay != null) {
            val timeOfDayDescr = if (timeOfDay == 120000L) "noon" else String.format("%d:%d", timeOfDay!! / 10000, timeOfDay!! / 100 % 100)
            result = if (result != null) String.format("%s at %s", result, timeOfDayDescr) else String.format("at %s", timeOfDayDescr)
        }
        return String.format("%s, %s", administratedQuantity, result)
    }
}

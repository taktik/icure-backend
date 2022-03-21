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

package org.taktik.icure.be.ehealth.dto.kmehr.v20161201

import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.DateType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.MomentType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*
import javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

object Utils {
    val xmlDtf = DatatypeFactory.newInstance()

    fun makeXMLGregorianCalendarFromHHMMSSLong(date: Long): XMLGregorianCalendar? {
        return xmlDtf.newXMLGregorianCalendar().apply {
            hour = (date / 10000 % 100).toInt()
            minute = (date / 100 % 100).toInt()
            second = (date % 100).toInt()
        }
    }

    fun makeXMLGregorianCalendarFromFuzzyLong(date: Long?): XMLGregorianCalendar? {
        return date?.let {
            if (it % 10000000000 == 0L) it / 10000000000 else if (it % 100000000 == 0L) it / 100000000 else if (it < 99991231 && it % 10000 == 0L) it / 10000 else if (it < 99991231 && it % 100 == 0L) it / 100 else it /*normalize*/
        }?.let { d ->
            xmlDtf.newXMLGregorianCalendar().apply {
                millisecond = FIELD_UNDEFINED
                timezone = FIELD_UNDEFINED

                hour = FIELD_UNDEFINED
                minute = FIELD_UNDEFINED
                second = FIELD_UNDEFINED

                try {
                    when (d) {
                        in 0..9999 -> {
                            year = d.toInt(); month = FIELD_UNDEFINED; day = FIELD_UNDEFINED
                        }
                        in 0..999912 -> {
                            year = (d / 100).toInt(); month = (d % 100).toInt(); day = FIELD_UNDEFINED
                        }
                        in 0..99991231 -> {
                            year = (d / 10000).toInt(); month = ((d / 100) % 100).toInt(); day = (d % 100).toInt()
                        }
                        else -> {
                            year = (d / 10000000000).toInt(); month = ((d / 100000000) % 100).toInt(); day = ((d / 1000000) % 100).toInt()
                            hour = ((d / 10000) % 100).toInt(); minute = ((d / 100) % 100).toInt(); second = (d % 100).toInt()
                        }
                    }
                }catch(ex : Exception) {
                    var tmp = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault())
                    year = tmp.year; month = tmp.monthValue; day = tmp.dayOfMonth
                    hour = tmp.hour; minute = tmp.minute; second = tmp.second
                }
            }
        }
    }

    fun makeDateTypeFromFuzzyLong(date: Long?): DateType? {
        return makeXMLGregorianCalendarFromFuzzyLong(date)?.let {
            DateType().apply {
                when (FIELD_UNDEFINED) {
                    it.month -> {
                        year = it
                    }
                    it.day -> {
                        yearmonth = it
                    }
                    it.hour -> {
                        this.date = it
                    }
                    else -> {
                        this.date = it; this.time = it
                    }
                }
            }
        }
    }

    fun makeMomentTypeFromFuzzyLong(date: Long?): MomentType? {
        return makeXMLGregorianCalendarFromFuzzyLong(date)?.let {
            MomentType().apply {
                when (FIELD_UNDEFINED) {
                    it.month -> {
                        year = it
                    }
                    it.day -> {
                        yearmonth = it
                    }
                    it.hour -> {
                        this.date = it
                    }
                    else -> {
                        this.date = it; this.time = it
                    }
                }
            }
        }
    }

    fun makeMomentTypeDateFromFuzzyLong(date: Long?): MomentType? {
        return makeXMLGregorianCalendarFromFuzzyLong(date)?.let {
            MomentType().apply {
                when (FIELD_UNDEFINED) {
                    it.month -> {
                        year = it
                    }
                    it.day -> {
                        yearmonth = it
                    }
                    it.hour -> {
                        this.date = it
                    }
                    else -> {
                        this.date = it
                    }
                }
            }
        }
    }

    fun makeFuzzyIntFromXMLGregorianCalendar(cal: XMLGregorianCalendar?): Int? {
        return cal?.let {
            it.year * 10000 + it.month * 100 + it.day
        }
    }

    fun makeXGC(date: Long?, unsetMillis: Boolean = false, setTimeZone: Boolean = false, timeZone: String = "Europe/Brussels"): XMLGregorianCalendar? {
        val zoneId = ZoneId.of(timeZone)
        return date?.let {
            val atZone = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId).atZone(zoneId)
            DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(atZone)).apply {
                timezone = if (setTimeZone) atZone.offset.totalSeconds / 60 else FIELD_UNDEFINED
                if (unsetMillis) {
                    millisecond = FIELD_UNDEFINED
                }
            }
        }
    }

    fun makeMomentType(instant: Instant, precision: ChronoUnit = ChronoUnit.SECONDS): org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.MomentType {
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return when (precision) {
            ChronoUnit.YEARS -> org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.MomentType().apply {
                year = xmlDtf.newXMLGregorianCalendarDate(dateTime.year, FIELD_UNDEFINED, FIELD_UNDEFINED, FIELD_UNDEFINED)
            }
            ChronoUnit.MONTHS -> org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.MomentType().apply {
                yearmonth = xmlDtf.newXMLGregorianCalendarDate(dateTime.year, dateTime.monthValue, FIELD_UNDEFINED, FIELD_UNDEFINED)
            }
            ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS, ChronoUnit.MILLIS -> {
                org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.MomentType().apply {
                    date = xmlDtf.newXMLGregorianCalendarDate(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth, FIELD_UNDEFINED)
                    time = when (precision) {
                        ChronoUnit.HOURS -> xmlDtf.newXMLGregorianCalendarTime(dateTime.hour, FIELD_UNDEFINED, FIELD_UNDEFINED, FIELD_UNDEFINED)
                        ChronoUnit.MINUTES -> xmlDtf.newXMLGregorianCalendarTime(dateTime.hour, dateTime.minute, FIELD_UNDEFINED, FIELD_UNDEFINED)
                        ChronoUnit.SECONDS -> xmlDtf.newXMLGregorianCalendarTime(dateTime.hour, dateTime.minute, dateTime.second, FIELD_UNDEFINED)
                        ChronoUnit.MILLIS -> xmlDtf.newXMLGregorianCalendarTime(dateTime.hour, dateTime.minute, dateTime.second, dateTime.get(ChronoField.MILLI_OF_SECOND), FIELD_UNDEFINED)
                        else -> null
                    }

                }
            }
            else -> throw IllegalArgumentException("unsupported precision $precision")
        }
    }


    fun makeXmlGregorianCalendar(instant: Instant): XMLGregorianCalendar {
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return xmlDtf.newXMLGregorianCalendar(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth, dateTime.hour, dateTime.minute, dateTime.second, FIELD_UNDEFINED, FIELD_UNDEFINED)
    }


    fun makeFuzzyLongFromXMLGregorianCalendar(cal: XMLGregorianCalendar?): Long? {
        return makeFuzzyIntFromXMLGregorianCalendar(cal)?.let {
            (it * 1000000L + (cal!!.hour ?: 0) * 10000 + (cal.minute ?: 0) * 100 + (cal.second ?: 0))
        }
    }

    fun makeFuzzyLongFromDateAndTime(date: XMLGregorianCalendar?, time: XMLGregorianCalendar?): Long? {
        return makeFuzzyIntFromXMLGregorianCalendar(date)?.let { d ->
            time?.let {
                d * 1000000L + (it.hour ?: 0) * 10000 + (it.minute ?: 0) * 100 + (it.second ?: 0)
            } ?: d.toLong()
        }
    }

    fun makeFuzzyLongFromMomentType(moment: org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.MomentType): Long? {
        if(moment.year != null) {
            return makeFuzzyLongFromDateAndTime(moment.year, moment.time)
        } else if(moment.yearmonth != null) {
            return makeFuzzyLongFromDateAndTime(moment.yearmonth, moment.time)
        } else {
            return makeFuzzyLongFromDateAndTime(moment.date, moment.time)
        }
    }
}

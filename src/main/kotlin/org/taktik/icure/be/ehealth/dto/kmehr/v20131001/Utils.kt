/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.dto.kmehr.v20131001

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.MomentType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.DateType
import java.time.*
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*

import javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class Utils {

	companion object {
		@JvmStatic
		fun makeXMLGregorianCalendarFromFuzzyLong(date : Long?) : XMLGregorianCalendarImpl? {
            return date?.let {
                if (it%10000000000 == 0L) it/10000000000 else if (it%100000000 == 0L) it/100000000 else if (it<99991231 && it%10000 == 0L) it/10000 else if (it<99991231 && it%100 == 0L) it/100 else it /*normalize*/
            }?.let { d ->
                XMLGregorianCalendarImpl().apply {
                    millisecond = FIELD_UNDEFINED
                    timezone = FIELD_UNDEFINED

					hour = FIELD_UNDEFINED
					minute = FIELD_UNDEFINED
					second = FIELD_UNDEFINED

					try {
						when (d) {
							in 0..9999 -> { year = d.toInt(); month = FIELD_UNDEFINED; day = FIELD_UNDEFINED }
							in 0..999912 -> { year = (d / 100).toInt(); month = (d % 100).toInt(); day = FIELD_UNDEFINED }
							in 0..99991231 -> { year = (d / 10000).toInt(); month = ((d / 100) % 100).toInt(); day = (d % 100).toInt() }
							else -> {
								year = (d / 10000000000).toInt(); month = ((d / 100000000) % 100).toInt(); day = ((d / 1000000) % 100).toInt()
								hour = ((d / 10000) % 100).toInt(); minute = ((d / 100) % 100).toInt(); second = (d % 100).toInt()
							}
						}
					} catch (ignored: IllegalArgumentException) {}
                }
            }
        }

		@JvmStatic
		fun makeDateTypeFromFuzzyLong(date : Long?) : DateType? {
            return Companion.makeXMLGregorianCalendarFromFuzzyLong(date)?.let {
                DateType().apply {
                    when (FIELD_UNDEFINED) {
                        it.month -> { year = it }
                        it.day -> { yearmonth = it }
                        it.hour -> { this.date = it }
                        else -> { this.date = it; this.time = it }
                    }
                }
            }
        }

		@JvmStatic
		fun makeMomentTypeFromFuzzyLong(date : Long?) : MomentType? {
            return Companion.makeXMLGregorianCalendarFromFuzzyLong(date)?.let {
                MomentType().apply {
                    when (FIELD_UNDEFINED) {
                        it.month -> { year = it }
                        it.day -> { yearmonth = it }
                        it.hour -> { this.date = it }
                        else -> { this.date = it; this.time = it }
                    }
                }
            }
        }

		@JvmStatic
		fun makeXGC(epochMillisTimestamp: Long?, unsetMillis : Boolean = false): XMLGregorianCalendar? {
            return epochMillisTimestamp?.let {
                DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(GregorianCalendar.getInstance().apply { time = Date(epochMillisTimestamp) } as GregorianCalendar)
                    .apply {
						timezone = FIELD_UNDEFINED
						if (unsetMillis) {
							millisecond = FIELD_UNDEFINED
						}
					}
            }
        }

		@JvmStatic
		fun makeMomentType(instant: Instant, precision: ChronoUnit = ChronoUnit.SECONDS) : MomentType {
			val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            return when (precision) {
                ChronoUnit.YEARS -> MomentType().apply {
                    year = XMLGregorianCalendarImpl.createDate(dateTime.year, FIELD_UNDEFINED, FIELD_UNDEFINED, FIELD_UNDEFINED)
                }
                ChronoUnit.MONTHS -> MomentType().apply {
                    yearmonth = XMLGregorianCalendarImpl.createDate(dateTime.year, dateTime.monthValue, FIELD_UNDEFINED, FIELD_UNDEFINED)
                }
                ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS, ChronoUnit.MILLIS -> {
					MomentType().apply {
                        date = XMLGregorianCalendarImpl.createDate(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth, FIELD_UNDEFINED)
                        time = when(precision) {
                            ChronoUnit.HOURS -> XMLGregorianCalendarImpl.createTime(dateTime.hour, FIELD_UNDEFINED, FIELD_UNDEFINED, FIELD_UNDEFINED)
                            ChronoUnit.MINUTES -> XMLGregorianCalendarImpl.createTime(dateTime.hour, dateTime.minute, FIELD_UNDEFINED, FIELD_UNDEFINED)
                            ChronoUnit.SECONDS -> XMLGregorianCalendarImpl.createTime(dateTime.hour, dateTime.minute, dateTime.second, FIELD_UNDEFINED)
                            ChronoUnit.MILLIS -> XMLGregorianCalendarImpl.createTime(dateTime.hour, dateTime.minute, dateTime.second, dateTime.get(ChronoField.MILLI_OF_SECOND), FIELD_UNDEFINED)
                            else -> null
                        }

                    }
				}
                else -> throw IllegalArgumentException("unsupported precision $precision")
            }
        }

		@JvmStatic
		fun makeXmlGregorianCalendar(instant: Instant): XMLGregorianCalendar {
			val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
			return XMLGregorianCalendarImpl.createDateTime(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth, dateTime.hour, dateTime.minute, dateTime.second, FIELD_UNDEFINED, FIELD_UNDEFINED)
		}

        fun makeFuzzyIntFromXMLGregorianCalendar(cal: XMLGregorianCalendar?) : Int? {
            return cal?.let {
                it.year*10000+it.month*100+it.day
            }
        }

        fun makeFuzzyLongFromXMLGregorianCalendar(cal: XMLGregorianCalendar?) : Long? {
            return makeFuzzyIntFromXMLGregorianCalendar(cal)?.let { (it * 1000000L + (cal!!.hour ?: 0)*10000+(cal.minute ?: 0)*100+(cal.second ?: 0)) }
        }

        fun makeFuzzyLongFromDateAndTime(date: XMLGregorianCalendar?, time: XMLGregorianCalendar?) : Long? {
            return makeFuzzyIntFromXMLGregorianCalendar(date)?.let { d -> time?.let { d * 1000000L + (it.hour ?: 0)*10000+(it.minute ?: 0)*100+(it.second ?: 0) } ?: d.toLong() }
        }
	}
}

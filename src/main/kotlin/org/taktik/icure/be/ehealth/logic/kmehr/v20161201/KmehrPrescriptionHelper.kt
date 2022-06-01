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

package org.taktik.icure.be.ehealth.logic.kmehr.v20161201

import java.math.BigDecimal
import java.time.temporal.ChronoUnit
import javax.xml.datatype.DatatypeFactory
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDDAYPERIOD
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDDAYPERIODvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDPERIODICITY
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTIMEUNIT
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTIMEUNITschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.DayperiodType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.DurationType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.FrequencyType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.PeriodicityType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TimequantityType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TimeunitType
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Duration
import org.taktik.icure.entities.embed.RegimenItem
import org.taktik.icure.utils.FuzzyValues

object KmehrPrescriptionHelper {
	val xmlDtf = DatatypeFactory.newInstance()

	fun inferPeriodFromRegimen(intakes: List<RegimenItem>?, frequency: CodeStub?): Period? {
		if (intakes == null) {
			return null
		}
		if (frequency != null) {
			return inferPeriodFromFrequency(frequency)
		}

		intakes.forEach { assertValidRegimen(it) }
		return when (intakes.size) {
			0 -> inferPeriodFromFrequency(frequency)
			1 -> intakes[0].let { intake ->
				if (isDaily(intake)) {
					Period(ChronoUnit.DAYS, 1)
				} else if (intake.weekday?.weekday?.code != null && intake.weekday?.weekNumber == null && intake.weekday?.weekday?.type == "CD-WEEKDAY") {
					Period(ChronoUnit.WEEKS, 1)
				} else inferPeriodFromFrequency(frequency)
			}
			else -> when (getCommonField(intakes)) {
				"date" -> getPeriodByDate(intakes)
				"dayNumber" -> getPeriodByDayNumber(intakes)
				"weekday" -> getPeriodByWeekDay(intakes)
				"daily" -> Period(
					ChronoUnit.DAYS,
					1
				) // not looking into the intake hours: currently supporting >= DAYS (see precisionBelowDaysNotSupportedDaily test)
				else -> inferPeriodFromFrequency(frequency)
			}
		}
	}

	fun inferPeriodFromFrequency(frequency: CodeStub?): Period? {
		return when (frequency?.code) {
			"UH" -> Period(ChronoUnit.MINUTES, 30)
			"U" -> Period(ChronoUnit.HOURS, 1)
			"UT" -> Period(ChronoUnit.HOURS, 2)
			"UD" -> Period(ChronoUnit.HOURS, 3)
			"UV" -> Period(ChronoUnit.HOURS, 4)
			"UQ" -> Period(ChronoUnit.HOURS, 5)
			"UZ" -> Period(ChronoUnit.HOURS, 6)
			"US" -> Period(ChronoUnit.HOURS, 7)
			"UA" -> Period(ChronoUnit.HOURS, 8)
			"UN" -> Period(ChronoUnit.HOURS, 9)
			"UX" -> Period(ChronoUnit.HOURS, 10)
			"UE" -> Period(ChronoUnit.HOURS, 11)
			"UW" -> Period(ChronoUnit.HOURS, 12)
			"D" -> Period(ChronoUnit.DAYS, 1)
			"DT" -> Period(ChronoUnit.DAYS, 2)
			"DD" -> Period(ChronoUnit.DAYS, 3)
			"DV" -> Period(ChronoUnit.DAYS, 4)
			"DQ" -> Period(ChronoUnit.DAYS, 5)
			"DZ" -> Period(ChronoUnit.DAYS, 6)
			"W" -> Period(ChronoUnit.WEEKS, 1)
			"DA" -> Period(ChronoUnit.DAYS, 8)
			"DN" -> Period(ChronoUnit.DAYS, 9)
			"DX" -> Period(ChronoUnit.DAYS, 10)
			"DE" -> Period(ChronoUnit.DAYS, 11)
			"DW" -> Period(ChronoUnit.DAYS, 12)
			"WT" -> Period(ChronoUnit.WEEKS, 2)
			"WD" -> Period(ChronoUnit.WEEKS, 3)
			"WV" -> Period(ChronoUnit.WEEKS, 4)
			"M" -> Period(ChronoUnit.MONTHS, 1)
			"WQ" -> Period(ChronoUnit.WEEKS, 5)
			"WZ" -> Period(ChronoUnit.WEEKS, 6)
			"WS" -> Period(ChronoUnit.WEEKS, 7)
			"WA" -> Period(ChronoUnit.WEEKS, 8)
			"MT" -> Period(ChronoUnit.MONTHS, 2)
			"WN" -> Period(ChronoUnit.WEEKS, 9)
			"WX" -> Period(ChronoUnit.WEEKS, 10)
			"WE" -> Period(ChronoUnit.WEEKS, 11)
			"WW" -> Period(ChronoUnit.WEEKS, 12)
			"MD" -> Period(ChronoUnit.MONTHS, 3)
			"MV" -> Period(ChronoUnit.MONTHS, 4)
			"MQ" -> Period(ChronoUnit.MONTHS, 5)
			"WP" -> Period(ChronoUnit.WEEKS, 24)
			"JH2" -> Period(ChronoUnit.DAYS, 183)
			"MZ2" -> Period(ChronoUnit.MONTHS, 6)
			"MS" -> Period(ChronoUnit.MONTHS, 7)
			"MA" -> Period(ChronoUnit.MONTHS, 8)
			"MN" -> Period(ChronoUnit.MONTHS, 9)
			"MX" -> Period(ChronoUnit.MONTHS, 10)
			"ME" -> Period(ChronoUnit.MONTHS, 11)
			"J" -> Period(ChronoUnit.YEARS, 1)
			"MC" -> Period(ChronoUnit.MONTHS, 18)
			"JT" -> Period(ChronoUnit.YEARS, 2)
			"JD" -> Period(ChronoUnit.YEARS, 3)
			"JV" -> Period(ChronoUnit.YEARS, 4)
			"JQ" -> Period(ChronoUnit.YEARS, 5)
			"JZ" -> Period(ChronoUnit.YEARS, 6)
			else -> null
		}
	}

	private fun isDaily(intake: RegimenItem) =
		intake.date == null && intake.dayNumber == null && intake.weekday == null

	private fun assertValidRegimen(intake: RegimenItem) {
		val dayFields = listOf(intake.date, intake.dayNumber, intake.weekday)
		require(dayFields.filterNotNull().size <= 1) { dayFields }
		val intakeMoments = listOf(intake.timeOfDay, intake.dayPeriod)
		require(intakeMoments.filterNotNull().size <= 1) { intakeMoments }
		intake.weekday?.let { weekday -> require(weekday.weekday?.let { it.type == "CD-WEEKDAY" } ?: false) }
	}

	private fun getCommonField(intakes: List<RegimenItem>): String {
		if (intakes.all { isDaily(it) }) {
			return "daily"
		}
		val ret = ThrowIfSetTwice("none")
		if (intakes.all { it.date != null }) {
			ret.set("date")
		}
		if (intakes.all { it.dayNumber != null }) {
			ret.set("dayNumber")
		}
		if (intakes.all { it.weekday != null }) {
			ret.set("weekday")
		}
		return ret.get()
	}

	private fun getPeriodByDate(intakes: List<RegimenItem>): Period? {
		if (!isSameTimeEachDay(intakes)) {
			return null
		}
		val secondsInterval =
			getRegularInterval(
				intakes.map { regimenItem -> regimenItem.date?.let { FuzzyValues.getDateTime(it) } }
			) { a, b -> ChronoUnit.SECONDS.between(a, b) }
		if (secondsInterval == null || secondsInterval == 0L) {
			return null
		}
		return Period(ChronoUnit.SECONDS, secondsInterval).toBiggestTimeUnit()
	}

	private fun getPeriodByDayNumber(intakes: List<RegimenItem>): Period? {
		if (!isSameTimeEachDay(intakes)) {
			return null
		}
		val interval = getRegularInterval(intakes.map { it.dayNumber ?: 0 }.sorted()) { a, b -> b.toLong() - a }
		if (interval == null || interval == 0L) {
			return null
		}
		return Period(ChronoUnit.DAYS, interval).toBiggestTimeUnit()
	}

	private fun getPeriodByWeekDay(intakes: List<RegimenItem>): Period? {
		if (!isSameTimeEachDay(intakes)) {
			return null
		}
		if (intakes.mapNotNull { it.weekday?.weekday?.code }.toSet().size != 1) {
			return null
		}
		if (intakes.map { it.weekday?.weekNumber }.contains(null)) {
			return null
		}
		val interval =
			getRegularInterval(intakes.map { it.weekday?.weekNumber ?: 0 }.sorted()) { a, b -> b.toLong() - a }
		if (interval == null || interval == 0L) {
			return null
		}
		return Period(ChronoUnit.WEEKS, interval).toBiggestTimeUnit()
	}

	private fun isSameTimeEachDay(intakes: List<RegimenItem>) =
		(intakes.map { it.dayPeriod }.toSet().size <= 1 && intakes.map { it.timeOfDay }.toSet().size <= 1)

	fun mapPeriodToFrequency(period: Period): FrequencyType {
		val frequency = FrequencyType()
		val periodCode = when (period.toBiggestTimeUnit()) {
			// when body generated with
			// perl -ne 'if (/^(\w+)\s+per\s+(\d+)\s+(\w+)/i) { $unit = uc $3 ; print "Period(ChronoUnit.$unit, $2) -> \"$1\"\n" }' tmp.txt
			// tmp.txt contains the copy of https://www.ehealth.fgov.be/standards/kmehr/content/page/tables/194/periodicity, edited to add 1 and "S" to singulars, convert half units to value in sub units
			Period(ChronoUnit.MINUTES, 30) -> "UH"
			Period(ChronoUnit.HOURS, 1) -> "U"
			Period(ChronoUnit.HOURS, 2) -> "UT"
			Period(ChronoUnit.HOURS, 3) -> "UD"
			Period(ChronoUnit.HOURS, 4) -> "UV"
			Period(ChronoUnit.HOURS, 5) -> "UQ"
			Period(ChronoUnit.HOURS, 6) -> "UZ"
			Period(ChronoUnit.HOURS, 7) -> "US"
			Period(ChronoUnit.HOURS, 8) -> "UA"
			Period(ChronoUnit.HOURS, 9) -> "UN"
			Period(ChronoUnit.HOURS, 10) -> "UX"
			Period(ChronoUnit.HOURS, 11) -> "UE"
			Period(ChronoUnit.HOURS, 12) -> "UW"
			Period(ChronoUnit.DAYS, 1) -> "D"
			Period(ChronoUnit.DAYS, 2) -> "DT"
			Period(ChronoUnit.DAYS, 3) -> "DD"
			Period(ChronoUnit.DAYS, 4) -> "DV"
			Period(ChronoUnit.DAYS, 5) -> "DQ"
			Period(ChronoUnit.DAYS, 6) -> "DZ"
			Period(ChronoUnit.WEEKS, 1) -> "W"
			Period(ChronoUnit.DAYS, 8) -> "DA"
			Period(ChronoUnit.DAYS, 9) -> "DN"
			Period(ChronoUnit.DAYS, 10) -> "DX"
			Period(ChronoUnit.DAYS, 11) -> "DE"
			Period(ChronoUnit.DAYS, 12) -> "DW"
			Period(ChronoUnit.WEEKS, 2) -> "WT"
			Period(ChronoUnit.WEEKS, 3) -> "WD"
			Period(ChronoUnit.WEEKS, 4) -> "WV"
			Period(ChronoUnit.MONTHS, 1) -> "M"
			Period(ChronoUnit.WEEKS, 5) -> "WQ"
			Period(ChronoUnit.WEEKS, 6) -> "WZ"
			Period(ChronoUnit.WEEKS, 7) -> "WS"
			Period(ChronoUnit.WEEKS, 8) -> "WA"
			Period(ChronoUnit.MONTHS, 2) -> "MT"
			Period(ChronoUnit.WEEKS, 9) -> "WN"
			Period(ChronoUnit.WEEKS, 10) -> "WX"
			Period(ChronoUnit.WEEKS, 11) -> "WE"
			Period(ChronoUnit.WEEKS, 12) -> "WW"
			Period(ChronoUnit.MONTHS, 3) -> "MD"
			Period(ChronoUnit.MONTHS, 4) -> "MV"
			Period(ChronoUnit.MONTHS, 5) -> "MQ"
			Period(ChronoUnit.WEEKS, 24) -> "WP"
			Period(ChronoUnit.DAYS, 183) -> "JH2"
			Period(ChronoUnit.MONTHS, 6) -> "MZ2"
			Period(ChronoUnit.MONTHS, 7) -> "MS"
			Period(ChronoUnit.MONTHS, 8) -> "MA"
			Period(ChronoUnit.MONTHS, 9) -> "MN"
			Period(ChronoUnit.MONTHS, 10) -> "MX"
			Period(ChronoUnit.MONTHS, 11) -> "ME"
			Period(ChronoUnit.YEARS, 1) -> "J"
			Period(ChronoUnit.MONTHS, 18) -> "MC"
			Period(ChronoUnit.YEARS, 2) -> "JT"
			Period(ChronoUnit.YEARS, 3) -> "JD"
			Period(ChronoUnit.YEARS, 4) -> "JV"
			Period(ChronoUnit.YEARS, 5) -> "JQ"
			Period(ChronoUnit.YEARS, 6) -> "JZ"
			else -> null
		}
		if (periodCode != null) {
			frequency.periodicity = PeriodicityType().apply { cd = CDPERIODICITY().apply { s = "CD-PERIODICITY"; sv = "1.1"; value = periodCode } }
		} else {
			val timeUnit = toCdTimeUnit(period.unit)
			val actualTimeUnit = timeUnit ?: toCdTimeUnit(ChronoUnit.YEARS)
			val actualAmount = if (timeUnit != null) period.amount else period.toUnit(ChronoUnit.YEARS).amount
			frequency.apply {
				nominator = FrequencyType.Nominator().apply {
					quantity = TimequantityType().apply {
						decimal = BigDecimal(actualAmount)
						unit = TimeunitType().apply {
							cd = CDTIMEUNIT().apply { s = CDTIMEUNITschemes.CD_TIMEUNIT; sv = "2.1"; value = actualTimeUnit }
						}
					}
				}
				denominator = FrequencyType.Denominator().apply {
					quantity = TimequantityType().apply {
						decimal = BigDecimal.ONE
						unit = TimeunitType().apply {
							cd = CDTIMEUNIT().apply { s = CDTIMEUNITschemes.CD_TIMEUNIT; sv = "2.1"; value = actualTimeUnit }
						}
					}
				}
			}
		}
		return frequency
	}

	fun toCdTimeUnit(chronoUnit: ChronoUnit): String? {
		return when (chronoUnit) {
			// when body generated with
			// perl -ne 'if (/^(\w+)\s+(\w+?)(:?second)?\s*$/i) { $unit = uc $2 ; print "ChronoUnit.${unit}S -> \"$1\"\n" }' tmp.txt
			// tmp.txt contains the copy of https://www.ehealth.fgov.be/standards/kmehr/content/page/tables/244/time-unit
			ChronoUnit.YEARS -> "a"
			ChronoUnit.MONTHS -> "mo"
			ChronoUnit.WEEKS -> "wk"
			ChronoUnit.DAYS -> "d"
			ChronoUnit.HOURS -> "hr"
			ChronoUnit.MINUTES -> "min"
			ChronoUnit.SECONDS -> "s"
			ChronoUnit.MILLIS -> "ms"
			ChronoUnit.MICROS -> "us"
			ChronoUnit.NANOS -> "ns"
			else -> null
		}
	}

	fun toDurationType(d: Duration?): DurationType? {
		return d?.let { duration ->
			DurationType().apply {
				decimal = duration.value?.let { BigDecimal(it) }
				unit = TimeunitType().apply {
					cd = CDTIMEUNIT().apply { s = CDTIMEUNITschemes.CD_TIMEUNIT; sv = "2.1"; value = duration.unit?.code }
				}
			}
		}
	}

	fun toDaytime(intake: RegimenItem): ItemType.Regimen.Daytime {
		return ItemType.Regimen.Daytime().apply {
			if (intake.timeOfDay != null) {
				time = Utils.makeXMLGregorianCalendarFromHHMMSSLong(intake.timeOfDay!!)
			} else {
				val timeOfDay = intake.dayPeriod?.code ?: CDDAYPERIODvalues.DURINGLUNCH.value()
				when (timeOfDay) {
					CDDAYPERIODvalues.AFTERNOON.value() -> time = xmlDtf.newXMLGregorianCalendar("16:00:00")
					CDDAYPERIODvalues.EVENING.value() -> time = xmlDtf.newXMLGregorianCalendar("19:00:00")
					CDDAYPERIODvalues.NIGHT.value() -> time = xmlDtf.newXMLGregorianCalendar("22:00:00")
					CDDAYPERIODvalues.AFTERMEAL.value(), CDDAYPERIODvalues.BETWEENMEALS.value() -> throw IllegalArgumentException("$timeOfDay not supported: corresponds to multiple possible moments in a day")
					else -> dayperiod = DayperiodType().apply {
						cd = CDDAYPERIOD().apply { s = "CD-DAYPERIOD"; sv = "1.1"; value = CDDAYPERIODvalues.fromValue(timeOfDay) }
					}
				}
			}
		}
	}

	fun <E, I> getRegularInterval(elements: List<E>, intervalBetween: (E, E) -> I): I? {
		val interval = intervalBetween(elements[0], elements[1])
		var i = 2
		while (i < elements.size) {
			if (interval != intervalBetween(elements[i - 1], elements[i])) {
				return null
			}
			i++
		}
		return interval
	}

	data class Period(val unit: ChronoUnit, val amount: Long) {
		private val supportedUnits = ChronoUnit.values().filter { it >= ChronoUnit.SECONDS }

		fun toBiggestTimeUnit(): Period {
			val seconds = toUnit(ChronoUnit.SECONDS).amount
			val biggestUnit = supportedUnits.reversed().first { (seconds % it.duration.seconds) == 0L }
			return toUnit(biggestUnit)
		}

		fun toUnit(unit: ChronoUnit): Period {
			require(supportedUnits.contains(unit)) { "$unit not in $supportedUnits" }
			val seconds = amount * this.unit.duration.seconds
			return Period(unit, seconds / unit.duration.seconds)
		}
	}

	class ThrowIfSetTwice<T>(var value: T) {
		var assigned = false
			private set

		fun set(value: T) {
			if (assigned) {
				throw IllegalStateException("double assignment: current: ${this.value}; new: $value")
			}
			assigned = true
			this.value = value
		}

		fun get(): T {
			return value
		}
	}
}

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
package org.taktik.icure.services.external.rest.v2.dto

import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import org.taktik.icure.services.external.rest.v2.dto.be.mikrono.MikronoAppointmentDto
import org.taktik.icure.utils.FuzzyValues

class AppointmentDto(
	val zoneId: String? = null,
	val patientId: String? = null,
	val userId: String? = null,
	val prescriptorComment: String? = null,
	val patientComment: String? = null,
	val comment: String? = null,
	val type: String? = null,
	val location: String? = null,
	val status: String? = null,
	val paid: Boolean? = null,
	val amount: Double? = null,
	val startTime: Long? = null,
	val endTime: Long? = null
) : Serializable {

	constructor(a: MikronoAppointmentDto) : this(
		patientId = a.customerRef,
		userId = a.ownerRef,
		prescriptorComment = a.prescriptorComments,
		patientComment = a.customerComments,
		comment = a.comments,
		type = a.type,
		location = a.locationText,
		status = a.status,
		paid = a.paid,
		amount = a.price,
		startTime = FuzzyValues.getFuzzyDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(a.startTime), a.timezoneId?.let { ZoneId.of(a.timezoneId) } ?: ZoneId.systemDefault()), ChronoUnit.SECONDS),
		endTime = FuzzyValues.getFuzzyDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(a.endTime), a.timezoneId?.let { ZoneId.of(a.timezoneId) } ?: ZoneId.systemDefault()), ChronoUnit.SECONDS),
		zoneId = a.timezoneId
	)
}

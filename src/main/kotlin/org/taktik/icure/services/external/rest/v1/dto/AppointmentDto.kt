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

import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentDto
import org.taktik.icure.utils.FuzzyValues
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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

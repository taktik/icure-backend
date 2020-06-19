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
package org.taktik.icure.be.mikrono

import org.taktik.icure.dto.be.mikrono.EmailOrSmsMessage
import org.taktik.icure.services.external.rest.v1.dto.AppointmentDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.AppointmentImportDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentTypeRestDto

interface MikronoLogic {
    fun getPassword(licenseId: String): String
    fun register(serverUrl: String, userId: String?, token: String?): String?
    fun getMikronoServer(serverUrl: String?): String?
    fun sendMessage(serverUrl: String?, username: String, userToken: String, emailOrSmsMessage: EmailOrSmsMessage)
    fun getAppointmentsByDate(serverUrl: String?, username: String?, userToken: String?, ownerId: String?, calendarDate: Long?): List<AppointmentDto?>
    fun getAppointmentsByPatient(serverUrl: String?, username: String?, userToken: String?, ownerId: String?, patientId: String?, startTime: Long?, EndTime: Long?): List<AppointmentDto?>
    fun createAppointments(serverUrl: String?, username: String, userToken: String, appointments: List<AppointmentImportDto>): List<String>
    fun createAppointmentTypes(serverUrl: String?, username: String, userToken: String, appointmentTypes: List<MikronoAppointmentTypeRestDto?>): List<MikronoAppointmentTypeRestDto?>
}

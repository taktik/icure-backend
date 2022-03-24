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
package org.taktik.icure.services.external.rest.v1.dto

import java.io.Serializable

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
) : Serializable

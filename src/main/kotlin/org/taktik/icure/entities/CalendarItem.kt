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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
class CalendarItem : StoredICureDocument() {
    @NotNull
    var title: String? = null
    var calendarItemTypeId: String? = null
    var masterCalendarItemId: String? = null

    @Deprecated("")
    var patientId: String? = null
    var important: Boolean? = null
    var homeVisit: Boolean? = null
    var phoneNumber: String? = null
    var placeId: String? = null
    var address: Address? = null
    var addressText: String? = null

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var startTime // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var endTime // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null
    var confirmationTime // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
            : Long? = null
    var confirmationId: String? = null
    var duration: Long? = null
    var allDay: Boolean? = null
    var details: String? = null
    var wasMigrated: Boolean? = null

    @NotNull
    var agendaId: String? = null
    var meetingTags: Set<CalendarItemTag>? = null
    var flowItem: FlowItem? = null

}

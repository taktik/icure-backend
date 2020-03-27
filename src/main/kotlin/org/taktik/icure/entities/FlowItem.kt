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

@JsonInclude(JsonInclude.Include.NON_NULL)
class FlowItem {
    var id: String? = null
    var title: String? = null
    var comment: String? = null
    var receptionDate: Long? = null
    var processingDate: Long? = null
    var processer: String? = null
    var cancellationDate: Long? = null
    var canceller: String? = null
    var cancellationReason: String? = null
    var cancellationNote: String? = null
    var status: String? = null
    var homeVisit: Boolean? = null
    var municipality: String? = null
    var town: String? = null
    var zipCode: String? = null
    var street: String? = null
    var building: String? = null
    var buildingNumber: String? = null
    var doorbellName: String? = null
    var floor: String? = null
    var letterBox: String? = null
    var notesOps: String? = null
    var notesContact: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var type: String? = null
    var emergency: Boolean? = null
    var phoneNumber: String? = null
    var patientId: String? = null
    var patientLastName: String? = null
    var patientFirstName: String? = null

}

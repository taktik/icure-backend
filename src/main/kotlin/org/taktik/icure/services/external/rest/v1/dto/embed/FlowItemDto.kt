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
package org.taktik.icure.services.external.rest.v1.dto.embed


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class FlowItemDto(
        val id: String? = null,
        val title: String? = null,
        val comment: String? = null,
        val receptionDate: Long? = null,
        val processingDate: Long? = null,
        val processer: String? = null,
        val cancellationDate: Long? = null,
        val canceller: String? = null,
        val cancellationReason: String? = null,
        val cancellationNote: String? = null,
        val status: String? = null,
        val homeVisit: Boolean? = null,
        val municipality: String? = null,
        val town: String? = null,
        val zipCode: String? = null,
        val street: String? = null,
        val building: String? = null,
        val buildingNumber: String? = null,
        val doorbellName: String? = null,
        val floor: String? = null,
        val letterBox: String? = null,
        val notesOps: String? = null,
        val notesContact: String? = null,
        val latitude: String? = null,
        val longitude: String? = null,
        val type: String? = null,
        val emergency: Boolean? = null,
        val phoneNumber: String? = null,
        val patientId: String? = null,
        val patientLastName: String? = null,
        val patientFirstName: String? = null,
        val description: String? = null,
        val interventionCode: String? = nul
)

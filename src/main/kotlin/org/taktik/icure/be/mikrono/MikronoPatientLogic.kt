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
package org.taktik.icure.be.mikrono

import org.taktik.icure.be.mikrono.dto.ChangeExternalIDReplyDto
import org.taktik.icure.entities.Patient
import java.util.*

/**
 * Created by aduchate on 16/12/11, 12:59
 */
interface MikronoPatientLogic {
    fun createPatients(url: String?, patients: Collection<Patient>, mikronoUser: String, mikronoPassword: String): List<Long>
    fun loadPatient(url: String?, id: String?, mikronoUser: String, mikronoPassword: String): Patient?
    fun listPatients(url: String?, fromDate: Date?, mikronoUser: String, mikronoPassword: String): List<String>
    fun updateExternalIds(url: String?, ids: Map<String, String>?, mikronoUser: String, mikronoPassword: String): ChangeExternalIDReplyDto
    fun updatePatientId(url: String?, id: String?, externalId: String, mikronoUser: String, mikronoPassword: String)
    fun loadPatientWithIcureId(url: String?, id: String?, mikronoUser: String, mikronoPassword: String): Patient?
    fun updatePatients(url: String?, patients: Collection<Patient>, mikronoUser: String, mikronoPassword: String)
}

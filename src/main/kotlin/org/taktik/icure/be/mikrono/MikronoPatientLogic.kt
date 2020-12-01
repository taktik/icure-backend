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

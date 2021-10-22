/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.kmehr.patientinfo.impl.v20110701

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.logic.kmehr.patientinfo.PatientInfoFileLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient

@Service
class PatientInfoLogicImpl(val patientInfoFileExport: PatientInfoFileExport) : PatientInfoFileLogic {
    override fun createExport(patient: Patient, sender: HealthcareParty, language: String): Flow<DataBuffer> {
        return patientInfoFileExport.export(patient, sender, language)
    }
}

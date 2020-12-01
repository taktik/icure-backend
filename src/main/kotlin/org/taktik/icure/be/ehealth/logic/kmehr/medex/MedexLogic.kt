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

package org.taktik.icure.be.ehealth.logic.kmehr.medex

import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient

interface MedexLogic {
    suspend fun createMedex(
            author: HealthcareParty, patient: Patient, lang: String, incapacityType: String, incapacityReason: String, outOfHomeAllowed: Boolean, certificateDate: Long?,
            contentDate: Long?, beginDate: Long, endDate: Long, diagnosisICD: String?, diagnosisICPC: String?, diagnosisDescr: String?
    ): String;
}

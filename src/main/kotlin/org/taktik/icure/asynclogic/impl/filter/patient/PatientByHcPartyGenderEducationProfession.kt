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
package org.taktik.icure.asynclogic.impl.filter.patient

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.patient.PatientByHcPartyGenderEducationProfession
import org.taktik.icure.entities.Patient
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

@Service
class PatientByHcPartyGenderEducationProfession(private val patientLogic: PatientLogic,
                                                private val sessionLogic: AsyncSessionLogic) : Filter<String, Patient, PatientByHcPartyGenderEducationProfession> {

    override fun resolve(filter: PatientByHcPartyGenderEducationProfession, context: Filters) = flow<String> {
        try {
            emitAll(patientLogic.listByHcPartyGenderEducationProfessionIdsOnly(filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic), filter.gender, filter.education, filter.profession))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}

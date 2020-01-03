package org.taktik.icure.asynclogic.impl.filter.patient

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dto.filter.patient.PatientByHcPartyNameFilter
import org.taktik.icure.entities.Patient
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

class PatientByHcPartyNameFilter(private val patientLogic: PatientLogic,
                                 private val sessionLogic: AsyncSessionLogic) : Filter<String, Patient, PatientByHcPartyNameFilter> {

    override suspend fun resolve(filter: PatientByHcPartyNameFilter, context: Filters): Flow<String> {
        return try {
            patientLogic.listByHcPartyName(filter.name,
                    if (filter.healthcarePartyId != null) filter.healthcarePartyId else getLoggedHealthCarePartyId(sessionLogic))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}

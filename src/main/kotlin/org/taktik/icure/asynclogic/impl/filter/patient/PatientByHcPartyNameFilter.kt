package org.taktik.icure.asynclogic.impl.filter.patient

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.patient.PatientByHcPartyNameFilter
import org.taktik.icure.entities.Patient
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

class PatientByHcPartyNameFilter(private val patientLogic: PatientLogic,
                                 private val sessionLogic: AsyncSessionLogic) : Filter<String, Patient, PatientByHcPartyNameFilter> {

    override fun resolve(filter: PatientByHcPartyNameFilter, context: Filters) = flow<String> {
        try {
            emitAll(patientLogic.listByHcPartyName(filter.name, filter.healthcarePartyId ?: getLoggedHealthCarePartyId(sessionLogic)))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}

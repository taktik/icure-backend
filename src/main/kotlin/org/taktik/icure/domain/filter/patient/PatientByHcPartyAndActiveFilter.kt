package org.taktik.icure.domain.filter.patient

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Patient

interface PatientByHcPartyAndActiveFilter : Filter<String, Patient> {
    val active: Boolean
    val healthcarePartyId: String?
}

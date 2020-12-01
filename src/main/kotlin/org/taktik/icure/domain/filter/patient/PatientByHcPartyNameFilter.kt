package org.taktik.icure.domain.filter.patient

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Patient

interface PatientByHcPartyNameFilter : Filter<String, Patient> {
    val name: String?
    val healthcarePartyId: String?
}

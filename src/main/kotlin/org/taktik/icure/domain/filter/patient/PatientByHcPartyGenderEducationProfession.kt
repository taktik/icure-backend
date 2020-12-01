package org.taktik.icure.domain.filter.patient

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender

interface PatientByHcPartyGenderEducationProfession : Filter<String, Patient> {
    val gender: Gender?
    val education: String?
    val profession: String?
    val healthcarePartyId: String?
}

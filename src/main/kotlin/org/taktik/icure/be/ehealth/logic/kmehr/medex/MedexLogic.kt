package org.taktik.icure.be.ehealth.logic.kmehr.medex

import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient

interface MedexLogic {
    fun createMedex(
            author: HealthcareParty, patient: Patient, lang: String, incapacityType: String, incapacityReason: String, outOfHomeAllowed: Boolean, certificateDate: Long,
            contentDate: Long?, beginDate: Long, endDate: Long, diagnosisICD: String?, diagnosisICPC: String?, diagnosisDescr: String?
    ): String;
}
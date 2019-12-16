//package org.taktik.icure.asynclogic.impl.filter.patient
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.taktik.icure.asynclogic.impl.filter.Filter
//import org.taktik.icure.asynclogic.impl.filter.Filters
//import org.taktik.icure.dto.filter.patient.PatientByHcPartyNameFilter
//import org.taktik.icure.entities.Patient
//import org.taktik.icure.logic.ICureSessionLogic
//import org.taktik.icure.logic.PatientLogic
//import java.util.*
//import javax.security.auth.login.LoginException
//
//class PatientByHcPartyNameFilter : Filter<String?, Patient?, PatientByHcPartyNameFilter?> {
//    var patientLogic: PatientLogic? = null
//    var sessionLogic: ICureSessionLogic? = null
//    @Autowired
//    fun setPatientLogic(patientLogic: PatientLogic?) {
//        this.patientLogic = patientLogic
//    }
//
//    @Autowired
//    fun setSessionLogic(sessionLogic: ICureSessionLogic?) {
//        this.sessionLogic = sessionLogic
//    }
//
//    override fun resolve(filter: PatientByHcPartyNameFilter, context: Filters): Set<String> {
//        return try {
//            HashSet(patientLogic!!.listByHcPartyName(
//                    filter.name,
//                    if (filter.healthcarePartyId != null) filter.healthcarePartyId else loggedHealthCarePartyId))
//        } catch (e: LoginException) {
//            throw IllegalArgumentException(e)
//        }
//    }
//}

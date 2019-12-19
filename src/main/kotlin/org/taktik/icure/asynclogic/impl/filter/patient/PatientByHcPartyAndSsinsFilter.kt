///*
// * Copyright (C) 2018 Taktik SA
// *
// * This file is part of iCureBackend.
// *
// * iCureBackend is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 2 as published by
// * the Free Software Foundation.
// *
// * iCureBackend is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.taktik.icure.asynclogic.impl.filter.patient
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.taktik.icure.asynclogic.impl.filter.Filter
//import org.taktik.icure.asynclogic.impl.filter.Filters
//import org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinsFilter
//import org.taktik.icure.entities.Patient
//import org.taktik.icure.asynclogic.ICureSessionLogic
//import org.taktik.icure.asynclogic.PatientLogic
//import java.util.*
//import javax.security.auth.login.LoginException
//
//class PatientByHcPartyAndSsinsFilter : Filter<String?, Patient?, PatientByHcPartyAndSsinsFilter?> {
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
//    override fun resolve(filter: PatientByHcPartyAndSsinsFilter, context: Filters): Set<String> {
//        return try {
//            HashSet(patientLogic!!.listByHcPartyAndSsinsIdsOnly(filter.ssins, if (filter.healthcarePartyId != null) filter.healthcarePartyId else loggedHealthCarePartyId))
//        } catch (e: LoginException) {
//            throw IllegalArgumentException(e)
//        }
//    }
//}

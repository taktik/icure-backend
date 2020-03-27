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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.constants.Permissions.CriterionDataType
import org.taktik.icure.constants.Permissions.CriterionType
import org.taktik.icure.constants.Permissions.CriterionTypeCurrentUser
import org.taktik.icure.entities.User
import org.taktik.icure.security.PermissionSetIdentifier
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class PermissionCriterion : Cloneable, Serializable {
    var isNegative = false
    var currentUser: CriterionTypeCurrentUser? = null
    var dataType: CriterionDataType? = null
    var patientStatus: String? = null
    val type: CriterionType?
        get() {
            if (currentUser != null) {
                return CriterionType.CURRENT_USER
            } else if (dataType != null) {
                return CriterionType.DATA_TYPE
            } else if (patientStatus != null) {
                return CriterionType.PATIENT_STATUS
            }
            return null
        }

    val isUseless: Boolean
        get() = currentUser == null && dataType == null && patientStatus == null

    fun match(permissionSetIdentifier: PermissionSetIdentifier,
              dataCreationUserId: String?, dataModificationUserId: String?,
              patientCreationUserId: String?, patientModificationUserId: String?,
              patientReferenceHcParty: String?, patientHcPartiesTeam: Set<String?>?,
              matchedDataType: CriterionDataType?, matchedPatientStatus: String?): Boolean {
        var match = false
        if (currentUser != null) {
            val currentUserId = permissionSetIdentifier.getPrincipalIdOfClass(User::class.java)
            when (currentUser) {
                CriterionTypeCurrentUser.DATA_CREATION_USER -> match = dataCreationUserId != null && dataCreationUserId == currentUserId
                CriterionTypeCurrentUser.DATA_MODIFICATION_USER -> match = dataModificationUserId != null && dataModificationUserId == currentUserId
                CriterionTypeCurrentUser.PATIENT_CREATION_USER -> match = patientCreationUserId != null && patientCreationUserId == currentUserId
                CriterionTypeCurrentUser.PATIENT_MODIFICATION_USER -> match = patientModificationUserId != null && patientModificationUserId == currentUserId
                CriterionTypeCurrentUser.PATIENT_REFERENCE_HC_USER -> match = patientReferenceHcParty != null && patientReferenceHcParty == currentUserId
                CriterionTypeCurrentUser.PATIENT_HC_TEAM_USER -> match = patientHcPartiesTeam != null && patientHcPartiesTeam.contains(currentUserId)
            }
        } else if (matchedDataType != null) {
            return matchedDataType == dataType
        } else if (matchedPatientStatus != null) {
            return matchedPatientStatus == patientStatus
        }
        if (isNegative) {
            match = !match
        }
        return match
    }

    public override fun clone(): PermissionCriterion {
        val clone = PermissionCriterion()
        clone.isNegative = isNegative
        clone.currentUser = currentUser
        clone.dataType = dataType
        clone.patientStatus = patientStatus
        return clone
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as PermissionCriterion
        if (isNegative != that.isNegative) return false
        if (currentUser != that.currentUser) return false
        if (dataType != that.dataType) return false
        return if (if (patientStatus != null) patientStatus != that.patientStatus else that.patientStatus != null) false else true
    }

    override fun hashCode(): Int {
        var result = if (isNegative) 1 else 0
        result = 31 * result + if (currentUser != null) currentUser.hashCode() else 0
        result = 31 * result + if (dataType != null) dataType.hashCode() else 0
        result = 31 * result + if (patientStatus != null) patientStatus.hashCode() else 0
        return result
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

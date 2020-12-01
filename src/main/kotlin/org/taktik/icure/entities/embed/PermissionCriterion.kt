/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.constants.Permissions.CriterionDataType
import org.taktik.icure.constants.Permissions.CriterionType
import org.taktik.icure.constants.Permissions.CriterionTypeCurrentUser
import org.taktik.icure.entities.User
import org.taktik.icure.security.PermissionSetIdentifier
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PermissionCriterion(
        @JsonProperty("isNegative") val negative: Boolean = false,
        val currentUser: CriterionTypeCurrentUser? = null,
        val dataType: CriterionDataType? = null,
        val patientStatus: String? = null
) : Serializable {
    fun getType(): CriterionType? {
        if (currentUser != null) {
            return CriterionType.CURRENT_USER
        } else if (dataType != null) {
            return CriterionType.DATA_TYPE
        } else if (patientStatus != null) {
            return CriterionType.PATIENT_STATUS
        }
        return null
    }

    fun isUseless(): Boolean = currentUser == null && dataType == null && patientStatus == null

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
        if (negative) {
            match = !match
        }
        return match
    }
}

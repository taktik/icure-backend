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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.constants.Permissions
import org.taktik.icure.constants.Permissions.CriterionDataType
import org.taktik.icure.security.PermissionSetIdentifier
import java.io.Serializable
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Permission(var grant: Int = 0, var revoke: Int = 0, var criteria: MutableSet<PermissionCriterion?>? = HashSet()) : Cloneable, Serializable {

    fun grant(permissionType: Permissions.Type) {
        // Grant TYPE
        grant = grant or permissionType.bitValue

        // Make sure TYPE is not revoked
        revoke = revoke and permissionType.bitValue.inv()
    }

    fun revoke(permissionType: Permissions.Type) {
        // Revoke TYPE
        revoke = revoke or permissionType.bitValue

        // Make sure TYPE is not granted
        grant = grant and permissionType.bitValue.inv()
    }

    fun keep(permissionType: Permissions.Type) {
        // Make sure TYPE is not granted
        grant = grant and permissionType.bitValue.inv()

        // Make sure TYPE is not revoked
        revoke = revoke and permissionType.bitValue.inv()
    }

    @JsonIgnore
    fun isGranted(permissionType: Permissions.Type): Boolean {
        return permissionType.isEnabled(grant)
    }

    @JsonIgnore
    fun isRevoked(permissionType: Permissions.Type): Boolean {
        return permissionType.isEnabled(revoke)
    }

    @JsonIgnore
    fun isKept(permissionType: Permissions.Type): Boolean {
        return !isGranted(permissionType) && !isRevoked(permissionType)
    }

    protected fun canBeUsedWith(permissionType: Permissions.Type): Boolean {
        if (criteria != null && criteria!!.isNotEmpty()) {
            for (criterion in criteria!!) {
                if (!permissionType.isCriterionTypeSupported(criterion!!.getType())) {
                    return false
                }
            }
            return true
        }
        return permissionType.isNoCriterionSupported
    }


    @get:JsonIgnore
    val isUseless: Boolean
        get() = grant == 0 && revoke == 0

    fun conflictWith(permission: Permission): Boolean {
        return if (hasSameCriteriaAs(permission)) {
            grant and permission.revoke != 0 || revoke and permission.grant != 0
        } else false
    }

    fun hasNoCriteria(): Boolean {
        return criteria == null || criteria!!.isEmpty()
    }

    fun hasSameCriteriaAs(permission: Permission): Boolean {
        return if (criteria == null) {
            permission.criteria == null
        } else permission.criteria != null && criteria == permission.criteria
    }

    fun superScope(lookup: Permission): Boolean {
        // TODO improve this
        return hasNoCriteria() && !lookup.hasNoCriteria()
    }

    fun addToCriteria(value: PermissionCriterion?) {
        criteria!!.add(value)
    }

    fun removeFromCriteria(value: PermissionCriterion?) {
        criteria!!.remove(value)
    }

    fun countMatchedCriteria(permissionSetIdentifier: PermissionSetIdentifier, dataCreationUserId: String?, dataModificationUserId: String?,
                             patientCreationUserId: String?, patientModificationUserId: String?,
                             patientReferenceHcParty: String?, patientHcPartiesTeam: Set<String?>?,
                             matchedDataType: CriterionDataType?, matchedPatientStatus: String?): Int {
        var matched = 0
        if (criteria != null) {
            for (criterion in criteria!!) {
                if (criterion!!.match(permissionSetIdentifier, dataCreationUserId, dataModificationUserId, patientCreationUserId, patientModificationUserId, patientReferenceHcParty, patientHcPartiesTeam, matchedDataType, matchedPatientStatus)) {
                    matched++
                } else {
                    return -1
                }
            }
        }
        return matched
    }

    companion object {
        fun granted(vararg permissions: Permissions.Type): Permission {
            val p = Permission()
            for (pt in permissions) {
                p.grant(pt)
            }
            return p
        }
    }
}

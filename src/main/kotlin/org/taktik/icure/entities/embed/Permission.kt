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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.constants.Permissions
import org.taktik.icure.constants.Permissions.CriterionDataType
import org.taktik.icure.security.PermissionSetIdentifier
import java.io.Serializable
import java.util.HashSet

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Permission : Cloneable, Serializable {
    protected var grant = 0
    protected var revoke = 0
    protected var criteria: MutableSet<PermissionCriterion?>? = HashSet()
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

    fun getGrant(): Int {
        var grant = grant

        // Remove invalid grant TYPE
        for (permissionType in Permissions.Type.values()) {
            if (!canBeUsedWith(permissionType)) {
                grant = grant and permissionType.bitValue.inv()
            }
        }
        return grant
    }

    fun getRevoke(): Int {
        var revoke = revoke

        // Remove invalid revoke TYPE
        for (permissionType in Permissions.Type.values()) {
            if (!canBeUsedWith(permissionType)) {
                revoke = revoke and permissionType.bitValue.inv()
            }
        }
        return revoke
    }

    protected fun canBeUsedWith(permissionType: Permissions.Type): Boolean {
        if (getCriteria() != null && !getCriteria()!!.isEmpty()) {
            for (criterion in getCriteria()!!) {
                if (!permissionType.isCriterionTypeSupported(criterion.getType())) {
                    return false
                }
            }
            return true
        }
        return permissionType.isNoCriterionSupported
    }

    fun getCriteria(): Set<PermissionCriterion?>? {
        return criteria
    }

    fun setGrant(grant: Int) {
        this.grant = grant
    }

    fun setRevoke(revoke: Int) {
        this.revoke = revoke
    }

    @get:JsonIgnore
    val isUseless: Boolean
        get() = getGrant() == 0 && getRevoke() == 0

    fun conflictWith(permission: Permission): Boolean {
        return if (hasSameCriteriaAs(permission)) {
            getGrant() and permission.getRevoke() != 0 || getRevoke() and permission.getGrant() != 0
        } else false
    }

    fun hasNoCriteria(): Boolean {
        return criteria == null || criteria!!.isEmpty()
    }

    fun hasSameCriteriaAs(permission: Permission): Boolean {
        return if (criteria == null) {
            permission.getCriteria() == null
        } else permission.getCriteria() != null && criteria == permission.getCriteria()
    }

    fun superScope(lookup: Permission): Boolean {
        // TODO improve this
        return hasNoCriteria() && !lookup.hasNoCriteria()
    }

    fun setCriteria(value: MutableSet<PermissionCriterion?>?) {
        criteria = value
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

    public override fun clone(): Permission {
        val clone = Permission()
        clone.setGrant(getGrant())
        clone.setRevoke(getRevoke())
        if (criteria != null) {
            for (criterion in criteria!!) {
                val criterionClone = criterion!!.clone()
                clone.addToCriteria(criterionClone)
            }
        }
        return clone
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + grant
        result = prime * result + revoke
        result = prime * result + if (criteria == null) 0 else criteria.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Permission
        if (grant != other.grant) return false
        if (revoke != other.revoke) return false
        if (criteria == null) {
            if (other.criteria != null) return false
        } else if (criteria != other.criteria) return false
        return true
    }

    companion object {
        private const val serialVersionUID = 1L
        fun granted(vararg permissions: Permissions.Type): Permission {
            val p = Permission()
            for (pt in permissions) {
                p.grant(pt)
            }
            return p
        }
    }
}

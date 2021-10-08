/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security.database

import org.springframework.security.core.GrantedAuthority
import org.taktik.icure.entities.security.Permission
import org.taktik.icure.security.PermissionSetIdentifier
import org.taktik.icure.security.AbstractUserDetails

class DatabaseUserDetails(
        permissionSetIdentifier: PermissionSetIdentifier,
        authorities: Set<GrantedAuthority>,
        principalPermissions: Set<Permission>,
        val passwordHash: String?,
        val secret: String?,
        val use2fa: Boolean = false,
        val rev: String? = null,
        val applicationTokens: Map<String, String> = mapOf(),
        val application: String? = null,
        val groupIdUserIdMatching: List<String> = listOf()
) : AbstractUserDetails(
    permissionSetIdentifier, authorities, principalPermissions
) {
    override fun getPassword(): String? {
        return passwordHash
    }

    @Suppress("DuplicatedCode")
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is DatabaseUserDetails) return false
        if (!super.equals(o)) return false

        if (passwordHash != o.passwordHash) return false
        if (secret != o.secret) return false
        if (use2fa != o.use2fa) return false
        if (rev != o.rev) return false
        if (applicationTokens != o.applicationTokens) return false
        if (application != o.application) return false
        if (groupIdUserIdMatching != o.groupIdUserIdMatching) return false

        return true
    }

    @Suppress("DuplicatedCode")
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + passwordHash.hashCode()
        result = 31 * result + secret.hashCode()
        result = 31 * result + use2fa.hashCode()
        result = 31 * result + (rev?.hashCode() ?: 0)
        result = 31 * result + (applicationTokens?.hashCode() ?: 0)
        result = 31 * result + (application?.hashCode() ?: 0)
        result = 31 * result + (groupIdUserIdMatching?.hashCode() ?: 0)
        return result
    }

}

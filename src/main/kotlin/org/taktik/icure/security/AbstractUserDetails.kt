/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import org.springframework.security.core.GrantedAuthority
import org.taktik.icure.entities.security.Permission

abstract class AbstractUserDetails(
    override val permissionSetIdentifier: PermissionSetIdentifier,
    authorities: Set<GrantedAuthority>,
    protected var principalPermissions: Set<Permission>
) : UserDetails {
    val authoritiesSet:Set<GrantedAuthority> = authorities
    override fun getAuthorities(): Collection<GrantedAuthority> = authoritiesSet

    override var isRealAuth = true
    override var locale: String? = null
    override var logoutURL: String? = null
    override fun getUsername(): String {
        return permissionSetIdentifier!!.principalClass.name + ":" + permissionSetIdentifier!!.principalId
    }

    override fun getPassword(): String? {
        return null
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as AbstractUserDetails
        if (authorities != that.authorities) return false
        return permissionSetIdentifier == that.permissionSetIdentifier
    }

    override fun hashCode(): Int {
        var result = permissionSetIdentifier.hashCode()
        result = 31 * result + authorities.hashCode()
        return result
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

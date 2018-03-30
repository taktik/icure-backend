/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.logic.impl

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.taktik.icure.cache.SafeCache
import org.taktik.icure.cache.SafeMap
import org.taktik.icure.constants.Permissions
import org.taktik.icure.constants.Roles
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.Principal
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.logic.PermissionLogic
import org.taktik.icure.logic.RoleLogic
import org.taktik.icure.logic.UserLogic
import org.taktik.icure.security.PermissionSet
import org.taktik.icure.security.PermissionSetIdentifier
import java.util.HashSet

@Service
class PermissionLogicImpl(val permissionSetCache: Cache, val userLogic: UserLogic, val roleLogic: RoleLogic) : PermissionLogic {
	private val log = LoggerFactory.getLogger(PermissionLogicImpl::class.java)
	private val permissionSetSafeCache = SafeCache<String, PermissionSet>(permissionSetCache)

	override fun getPermissionSet(permissionSetIdentifier: PermissionSetIdentifier): PermissionSet? {
		return getValueFromCache(permissionSetSafeCache, permissionSetIdentifier, SafeMap.ValueProvider<PermissionSetIdentifier, PermissionSet?> { this.buildPermissionSet(it) })
	}

	private fun buildPermissionSet(permissionSetIdentifier: PermissionSetIdentifier?): PermissionSet? {
		if (permissionSetIdentifier != null) {
			log.debug("Creating permission set for {} #{}", permissionSetIdentifier.principalClass.simpleName, permissionSetIdentifier.principalId)

			// Get related principal logic
			val principalLogic = if (permissionSetIdentifier.principalClass == User::class.java) userLogic else if (permissionSetIdentifier.principalClass == Role::class.java) roleLogic else null
			if (principalLogic == null) {
				log.error("Cannot get related principal logic !")
				return null
			}

			// Build granted authorities before adding implicit permissions
			val grantedAuthorities = buildGrantedAuthorities(permissionSetIdentifier, hashSetOf())

			// Create PermissionSet
			val permissionSet = PermissionSet(permissionSetIdentifier, hashSetOf(), grantedAuthorities)

			log.debug("Done creating permission set for {} #{}", permissionSetIdentifier.principalClass.simpleName, permissionSetIdentifier.principalId)
			return permissionSet
		}

		return null
	}

	private fun buildGrantedAuthorities(permissionSetIdentifier: PermissionSetIdentifier, permissions: Set<Permission>): Set<GrantedAuthority> {
		val grantedAuthorities = HashSet<GrantedAuthority>()

		// Always allow ANONYMOUS authority
		grantedAuthorities.add(SimpleGrantedAuthority(Roles.GrantedAuthority.ROLE_ANONYMOUS))

		if (permissionSetIdentifier.principalId == "bootstrap") {
			grantedAuthorities.add(SimpleGrantedAuthority(Roles.GrantedAuthority.ROLE_BOOTSTRAP))
		} else {
			// Add USER authority if user can authenticate
			grantedAuthorities.add(SimpleGrantedAuthority(Roles.GrantedAuthority.ROLE_USER))

			// Add other authorities if any permission grant the corresponding permission type
			for (permission in permissions) {
				if (permission.isGranted(Permissions.Type.ADMIN)) {
					grantedAuthorities.add(SimpleGrantedAuthority(Roles.GrantedAuthority.ROLE_ADMINISTRATOR))
				}
			}
		}

		return grantedAuthorities
	}

	private fun buildKey(vararg objects: Any): String {
		val stringBuilder = StringBuilder()
		for (`object` in objects) {
			stringBuilder.append(`object`)
		}
		return stringBuilder.toString()
	}

	private fun getPermissionSetCacheKey(principalClass: Class<out Principal>, principalId: String): String {
		return buildKey(principalClass.name, principalId)
	}

	private fun getPermissionSetCacheKey(permissionSetIdentifier: PermissionSetIdentifier): String {
		return getPermissionSetCacheKey(permissionSetIdentifier.principalClass, permissionSetIdentifier.principalId)
	}

	private fun <V> getValueFromCache(cache: SafeCache<String, V>, permissionSetIdentifier: PermissionSetIdentifier?, valueProvider: SafeMap.ValueProvider<PermissionSetIdentifier, V?>): V? {
		return permissionSetIdentifier?.let {psi->
			cache.get(getPermissionSetCacheKey(psi)) {
				valueProvider.getValue(psi)
			}
		}
	}
}
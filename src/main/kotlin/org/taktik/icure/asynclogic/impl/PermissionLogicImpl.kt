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

package org.taktik.icure.asynclogic.impl

import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.taktik.icure.asynccache.AsyncSafeCache
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.PermissionLogic
import org.taktik.icure.constants.Roles
import org.taktik.icure.entities.User
import org.taktik.icure.entities.security.Permission
import org.taktik.icure.entities.security.PermissionType
import org.taktik.icure.entities.security.Principal
import org.taktik.icure.security.PermissionSetIdentifier
import org.taktik.icure.security.PermissionSetWithAuthorities

@Service
class PermissionLogicImpl(val permissionSetCache: Cache, val userDAO: UserDAO) : PermissionLogic {
	private val log = LoggerFactory.getLogger(PermissionLogicImpl::class.java)
	private val permissionSetSafeCache = AsyncSafeCache<PermissionSetIdentifier, PermissionSetWithAuthorities>(permissionSetCache)

	override suspend fun getPermissionSet(permissionSetIdentifier: PermissionSetIdentifier, principal: Principal?): PermissionSetWithAuthorities? {
		return getValueFromCache(
			permissionSetSafeCache, permissionSetIdentifier,
			object : AsyncSafeCache.AsyncValueProvider<PermissionSetIdentifier, PermissionSetWithAuthorities> {
				override suspend fun getValue(key: PermissionSetIdentifier) = buildPermissionSet(key, principal)
			}
		)
	}

	private suspend fun buildPermissionSet(permissionSetIdentifier: PermissionSetIdentifier?, principal: Principal?): PermissionSetWithAuthorities? {
		if (permissionSetIdentifier != null) {
			log.debug("Creating permission set for {} #{}", permissionSetIdentifier.principalClass.simpleName, permissionSetIdentifier.principalId)

			val permissions = principal?.permissions ?: when (permissionSetIdentifier.principalClass) {
				User::class.java -> userDAO.get(permissionSetIdentifier.principalId)?.permissions ?: setOf()
				else -> setOf()
			}

			// Build granted authorities before adding implicit permissions
			val grantedAuthorities = buildGrantedAuthorities(permissionSetIdentifier, permissions)

			// Create PermissionSet
			val permissionSet = PermissionSetWithAuthorities(permissionSetIdentifier, permissions, grantedAuthorities)

			log.debug("Done creating permission set for {} #{}", permissionSetIdentifier.principalClass.simpleName, permissionSetIdentifier.principalId)
			return permissionSet
		}

		return null
	}

	private fun buildGrantedAuthorities(permissionSetIdentifier: PermissionSetIdentifier, permissions: Set<Permission>): Set<GrantedAuthority> {
		val grantedAuthorities = HashSet<GrantedAuthority>()

		// Always allow ANONYMOUS authority
		grantedAuthorities.add(SimpleGrantedAuthority(Roles.GrantedAuthority.ROLE_ANONYMOUS))

		// Add USER authority if user can authenticate
		grantedAuthorities.add(SimpleGrantedAuthority(Roles.GrantedAuthority.ROLE_USER))

		// Add other authorities if any permission grant the corresponding permission type
		for (permission in permissions) {
			for (grant in permission.grants) {
				grantedAuthorities.add(if (grant.type == PermissionType.ADMIN) SimpleGrantedAuthority(Roles.GrantedAuthority.ROLE_ADMINISTRATOR) else SimpleGrantedAuthority(grant.type.name))
			}
		}
		return grantedAuthorities
	}

	private fun buildKey(vararg objects: Any): String {
		val stringBuilder = StringBuilder()
		for (part in objects) {
			stringBuilder.append(part)
		}
		return stringBuilder.toString()
	}

	private fun getPermissionSetCacheKey(principalClass: Class<out Principal>, principalId: String): String {
		return buildKey(principalClass.name, principalId)
	}

	private fun getPermissionSetCacheKey(permissionSetIdentifier: PermissionSetIdentifier): String {
		return getPermissionSetCacheKey(permissionSetIdentifier.principalClass, permissionSetIdentifier.principalId)
	}

	private suspend fun <V> getValueFromCache(cache: AsyncSafeCache<PermissionSetIdentifier, V>, permissionSetIdentifier: PermissionSetIdentifier?, valueProvider: AsyncSafeCache.AsyncValueProvider<PermissionSetIdentifier, V>): V? {
		return permissionSetIdentifier?.let { psi ->
			cache.get(psi, valueProvider)
		}
	}
}

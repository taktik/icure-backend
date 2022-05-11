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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PrincipalLogic
import org.taktik.icure.constants.Roles
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.base.PropertyTypeStub
import org.taktik.icure.entities.security.Permission
import org.taktik.icure.entities.security.Principal

abstract class PrincipalLogicImpl<P : Principal>(protected val roleDAO: RoleDAO, protected val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<P, GenericDAO<P>>(sessionLogic), PrincipalLogic<P> {

	protected val log = LoggerFactory.getLogger(javaClass)

	protected fun getParents(principal: Principal): Flow<Role> = flow {
		emitAll(roleDAO.getEntities(principal.getParents()))
	}

	override fun getProperties(principalId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<PropertyStub> = flow {
		val principal: Principal? = getPrincipal(principalId)
		principal?.let { emitAll(buildProperties(principal, includeDirect, includeHerited, includeDefault, mutableSetOf<PropertyTypeStub>())) }
			?: emitAll(emptyFlow<PropertyStub>())
	}

	override fun getPermissions(principalId: String, virtualHostId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<Permission> = flow {
		emitAll(emptyFlow<Permission>())
	}

	protected fun buildProperties(principal: Principal, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean, ignoredPropertyTypes: MutableSet<PropertyTypeStub>): Flow<PropertyStub> = flow {
		log.trace("buildProperties() : principal={}({}), includeDirect={}, includeHerited={}, includeDefault={}", principal.javaClass.simpleName, principal.id, includeDirect, includeHerited, includeDefault)
		// Prepare set of properties
		val properties = mutableSetOf<PropertyStub>()
		if (includeDirect) { // First add properties directly linked to the principal
			for (p in principal.properties) {
				if (p.type != null && !ignoredPropertyTypes.contains(p.type)) {
					ignoredPropertyTypes.add(p.type)
					properties.add(p)
				}
			}
		}
		if (includeHerited) { // Get the parent roles, sorted by natural order
			val parentRolesSorted = getParents(principal).toList().sortedWith(compareBy<Role, String>(String.CASE_INSENSITIVE_ORDER) { r -> r.name ?: "" })
			// Add properties directly linked to the parents
			for (parent in parentRolesSorted) {
				val parentProperties = buildProperties(parent, true, false, false, ignoredPropertyTypes).toList()
				properties.addAll(parentProperties)
			}
			// Add properties herited from grand parents
			for (parent in parentRolesSorted) {
				val parentProperties = buildProperties(parent, false, true, false, ignoredPropertyTypes).toList()
				properties.addAll(parentProperties)
			}
		}
		if (includeDefault) { // Get the default role and add property if not overridden in child role
			roleDAO.getRoleByName(Roles.DEFAULT_ROLE_NAME)?.let {
				for (defaultProp in it.properties) {
					if (!ignoredPropertyTypes.contains(defaultProp.type)) {
						properties.add(defaultProp)
					}
				}
			}
		}
		emitAll(properties.asFlow())
	}

	override fun getAscendantRoles(principalId: String): Flow<Role> = flow<Role> {
		getPrincipal(principalId)?.let { buildAscendantRoles(it, mutableSetOf()).collect { emit(it) } }
	}

	protected fun buildAscendantRoles(principal: Principal?, ignoredRoles: MutableSet<Role>): Flow<Role> = flow {
		val roles = mutableSetOf<Role>()
		principal?.let {
			if (principal is Role) {
				ignoredRoles.add(principal)
			}
			principal.getParents().let {
				getParents(principal).onEach {
					it.let {
						if (!ignoredRoles.contains(it)) {
							roles.add(it)
							roles.addAll(buildAscendantRoles(it, ignoredRoles).toList())
						}
					}
				}.collect()
			}
		}
		emitAll(roles.asFlow())
	}

	companion object {
		// Bit indexes
		protected var DEPENDENCY_INCLUDE_DIRECT = 1 shl 1
		protected var DEPENDENCY_INCLUDE_HERITED = 1 shl 2
	}
}

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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.constants.Roles
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.Property
import org.taktik.icure.entities.PropertyType
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.base.Principal
import org.taktik.icure.entities.embed.Permission

interface PrincipalLogic<P : Principal> {
    suspend fun getPrincipal(principalId: String): P?
    fun getProperties(principalId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<Property>
    fun getPermissions(principalId: String, virtualHostId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<Permission>
    fun getAscendantRoles(principalId: String): Flow<Role>
}

abstract class PrincipalLogicImpl<P : Principal>(protected val roleDAO: RoleDAO, protected val sessionLogic: AsyncSessionLogic)  : PrincipalLogic<P> {

    protected val log = LoggerFactory.getLogger(javaClass)

    protected fun getParents(principal: Principal): Flow<Role> = flow {
        val dbInstanceUri = sessionLogic.getCurrentSessionContext().getDbInstanceUri()
        val groupId = sessionLogic.getCurrentSessionContext().getGroupId()
        emitAll(roleDAO.getList(dbInstanceUri, groupId, principal.parents))
    }

    override fun getProperties(principalId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<Property> = flow<Property> {
        val principal: Principal? = getPrincipal(principalId)
        principal?.let { emitAll(buildProperties(principal, includeDirect, includeHerited, includeDefault, mutableSetOf())) }
                ?: emitAll(emptyFlow<Property>())
    }

    override fun getPermissions(principalId: String, virtualHostId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<Permission> = flow {
        emitAll(emptyFlow<Permission>())
    }

    protected fun buildProperties(principal: Principal, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean, ignoredPropertyTypes: MutableSet<PropertyType>): Flow<Property> = flow {
        log.trace("buildProperties() : principal={}({}), includeDirect={}, includeHerited={}, includeDefault={}", principal.javaClass.simpleName, principal.id, includeDirect, includeHerited, includeDefault)
        // Prepare set of properties
        val properties = mutableSetOf<Property>()
        if (includeDirect) { // First add properties directly linked to the principal
            for (p in principal.properties) {
                if (!ignoredPropertyTypes.contains(p.type)) {
                    ignoredPropertyTypes.add(p.type)
                    properties.add(p)
                }
            }
        }
        if (includeHerited) { // Get the parent roles, sorted by natural order
            val parentRolesSorted = getParents(principal).toList().sortedWith(kotlin.Comparator { r1, r2 -> r1.name.compareTo(r2.name, ignoreCase = true) })
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
            val dbInstanceUri = sessionLogic.getCurrentSessionContext().getDbInstanceUri()
            val groupId = sessionLogic.getCurrentSessionContext().getGroupId()
            roleDAO.getByName(dbInstanceUri, groupId, Roles.DEFAULT_ROLE_NAME)?.let {
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
            principal.parents?.let {
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

///*
// * Copyright (C) 2018 Taktik SA
// *
// * This file is part of iCureBackend.
// *
// * iCureBackend is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 2 as published by
// * the Free Software Foundation.
// *
// * iCureBackend is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.taktik.icure.asynclogic.impl
//
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.reactive.awaitSingle
//import kotlinx.coroutines.reactive.collect
//import org.slf4j.LoggerFactory
//import org.taktik.icure.asyncdao.RoleDAO
//import org.taktik.icure.constants.Roles
//import org.taktik.icure.entities.Property
//import org.taktik.icure.entities.PropertyType
//import org.taktik.icure.entities.Role
//import org.taktik.icure.entities.base.Principal
//import org.taktik.icure.entities.embed.Permission
//import reactor.core.publisher.Flux
//import java.net.URI
//import java.util.*
//
//interface PrincipalLogic<P : Principal?> {
//    fun getPrincipal(dbInstanceUrl: URI, groupId: String, principalId: String): P?
//    fun getProperties(principalId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flux<Property>
//    fun getPermissions(principalId: String, virtualHostId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flux<Permission>
//    fun getAscendantRoles(principalId: String?): Set<Role?>?
//}
//
//abstract class PrincipalLogicImpl<P : Principal?>(protected val roleDAO: RoleDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Role, RoleDAO>(sessionLogic), PrincipalLogic<P> {
//
//    protected val log = LoggerFactory.getLogger(javaClass)
//
//    protected fun getParents(principal: Principal): Flux<Role> = injectReactorContext(
//            flow {
//                val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.awaitSingle()!!
//                val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.awaitSingle()!!
//                roleDAO.getList(dbInstanceUri, groupId, principal.parents).collect {
//                    emit(it)
//                }
//            }
//    )
//
//    override fun getProperties(principalId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flux<Property> = injectReactorContext(
//            flow {
//                val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.awaitSingle()!!
//                val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.awaitSingle()!!
//                val principal: Principal? = getPrincipal(dbInstanceUri, groupId, principalId)
//                principal?.let { buildProperties(principal, includeDirect, includeHerited, includeDefault, mutableSetOf()) }?.collect {
//                    emit(it)
//                } ?: Flux.empty<Property>().collect { emit(it) }
//            }
//    )
//
//    override fun getPermissions(principalId: String, virtualHostId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flux<Permission> {
//        return Flux.empty()
//    }
//
//    protected suspend fun buildProperties(principal: Principal, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean, ignoredPropertyTypes: MutableSet<PropertyType>): Flux<Property> {
//        log.trace("buildProperties() : principal={}({}), includeDirect={}, includeHerited={}, includeDefault={}", principal.javaClass.simpleName, principal.id, includeDirect, includeHerited, includeDefault)
//        // Prepare set of properties
//        val properties = mutableSetOf<Property>()
//        if (includeDirect) { // First add properties directly linked to the principal
//            val principalProperties = principal.properties
//            for (p in principalProperties) {
//                if (!ignoredPropertyTypes.contains(p.type)) {
//                    ignoredPropertyTypes.add(p.type)
//                    properties.add(p)
//                }
//            }
//        }
//        if (includeHerited) { // Get the parent roles, sorted by natural order
//            val parentRolesSorted = getParents(principal).collectSortedList { r1: Role, r2: Role -> r1.name.compareTo(r2.name, ignoreCase = true) }.awaitSingle()
//            // Add properties directly linked to the parents
//            for (parent in parentRolesSorted) {
//                val parentProperties = buildProperties(parent, true, false, false, ignoredPropertyTypes)
//                properties.addAll(parentProperties)
//            }
//            // Add properties herited from grand parents
//            for (parent in parentRolesSorted) {
//                val parentProperties = buildProperties(parent, false, true, false, ignoredPropertyTypes)
//                properties.addAll(parentProperties)
//            }
//        }
//        if (includeDefault) { // Get the default role and add property if not overridden in child role
//            val defaultRole = roleDAO!!.getByName(Roles.DEFAULT_ROLE_NAME)
//            if (defaultRole != null) {
//                val defaultProperties = defaultRole.properties
//                for (defaultProp in defaultProperties) {
//                    if (!ignoredPropertyTypes.contains(defaultProp.type)) {
//                        properties.add(defaultProp)
//                    }
//                }
//            }
//        }
//        return properties
//    }
//
//    override fun getAscendantRoles(principalId: String): Set<Role> {
//        val principal: Principal? = getPrincipal(principalId)
//        return if (principal != null) {
//            buildAscendantRoles(principal, HashSet())
//        } else null
//    }
//
//    protected fun buildAscendantRoles(principal: Principal?, ignoredRoles: MutableSet<Role?>): Set<Role> {
//        val roles: MutableSet<Role> = HashSet()
//        if (principal != null) { // Add this role to ignore list
//            if (principal is Role) {
//                ignoredRoles.add(principal as Role?)
//            }
//            // Process parents
//            if (principal.parents != null) {
//                for (parent in getParents(principal)) {
//                    if (parent != null) {
//                        if (!ignoredRoles.contains(parent)) {
//                            roles.add(parent)
//                            roles.addAll(buildAscendantRoles(parent, ignoredRoles))
//                        }
//                    }
//                }
//            }
//        }
//        return roles
//    }
//
//    companion object {
//        // Bit indexes
//        protected var DEPENDENCY_INCLUDE_DIRECT = 1 shl 1
//        protected var DEPENDENCY_INCLUDE_HERITED = 1 shl 2
//    }
//}

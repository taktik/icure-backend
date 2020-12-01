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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.RoleLogic
import org.taktik.icure.constants.Permissions
import org.taktik.icure.constants.Roles
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Permission

@ExperimentalCoroutinesApi
@Transactional
@Service
class RoleLogicImpl(private val userDAO: UserDAO, sessionLogic: AsyncSessionLogic, roleDAO: RoleDAO) : PrincipalLogicImpl<Role>(roleDAO, sessionLogic), RoleLogic {

    override suspend fun getRoleByName(name: String): Role? {
        return roleDAO.getByName(name)
    }

    override fun getDescendantRoles(roleId: String) = flow<Role> {
        val role = getRole(roleId)
        role?.let {
            emitAll(getDescendantRoles(it, mutableSetOf()))
        }
    }

    private fun getDescendantRoles(role: Role, ignoredRoles: MutableSet<Role>): Flow<Role> = flow {
        ignoredRoles.add(role)

        // Process children
        role.children?.let {
            getChildren(role)
                    .filterNotNull()
                    .filter { !ignoredRoles.contains(it) }
                    .onEach { r: Role ->
                        emit(r)
                        emitAll(getDescendantRoles(r, ignoredRoles))
                    }
        }
    }

    override suspend fun getRole(id: String): Role? {
        return roleDAO.get(id)
    }

    override fun getUsers(role: Role) = flow<User> {
        emitAll(userDAO.getList(role.users))
    }

    override suspend fun createDefaultRoleIfNecessary() {
        getRoleByName(Roles.DEFAULT_ROLE_NAME)?.let {
            return
        }
        saveRole(Role(
                id = Roles.DEFAULT_ROLE_NAME,
                name = Roles.DEFAULT_ROLE_NAME,
                permissions = setOf(Permission.granted(Permissions.Type.AUTHENTICATE))
        ))
    }

    private suspend fun saveRole(role: Role): Role? { // Save role
        return roleDAO.save(role)
    }

    override suspend fun newRole(role: Role): Role? {
        return saveRole(role)
    }

    private fun getChildren(role: Role) = flow<Role> {
        emitAll(roleDAO.getList(role.children))
    }

    override fun createEntities(entities: Collection<Role>) = flow {
        emitAll(roleDAO.create(entities))
    }

    @Throws(Exception::class)
    override fun updateEntities(roles: Collection<Role>) = flow {
        roles.map { role: Role -> saveRole(role) }
                .filterNotNull()
                .onEach { emit(it) }
    }

    override fun getAllEntities() = flow() {
        emitAll(roleDAO.getAll())
    }

    override fun getAllEntityIds() = flow<String> {
        emitAll(roleDAO.getAll().mapNotNull { it.id })
    }

    override suspend fun exists(id: String): Boolean {
        return roleDAO.contains(id)
    }

    override suspend fun hasEntities(): Boolean {
        return roleDAO.hasAny()
    }

    override suspend fun getEntity(id: String): Role? {
        return getRole(id)
    }

    override suspend fun getPrincipal(roleId: String): Role? {
        return getRole(roleId)
    }

    override fun getGenericDAO(): GenericDAO<Role> {
        return roleDAO
    }
}

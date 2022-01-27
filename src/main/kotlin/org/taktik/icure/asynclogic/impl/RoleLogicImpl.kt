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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.RoleLogic
import org.taktik.icure.constants.Roles
import org.taktik.icure.entities.Role

@ExperimentalCoroutinesApi
@Service
class RoleLogicImpl(private val userDAO: UserDAO, sessionLogic: AsyncSessionLogic, roleDAO: RoleDAO) : PrincipalLogicImpl<Role>(roleDAO, sessionLogic), RoleLogic {

    override suspend fun getRoleByName(name: String): Role? {
        return roleDAO.getRoleByName(name)
    }

    override suspend fun getRole(id: String): Role? {
        return roleDAO.get(id)
    }

    override suspend fun createDefaultRoleIfNecessary() {
        getRoleByName(Roles.DEFAULT_ROLE_NAME)?.let {
            return
        }
        saveRole(Role(
                id = Roles.DEFAULT_ROLE_NAME,
                name = Roles.DEFAULT_ROLE_NAME,
                permissions = setOf()
        ))
    }

    private suspend fun saveRole(role: Role): Role? { // Save role
        return roleDAO.save(role)
    }

    override suspend fun newRole(role: Role): Role? {
        return saveRole(role)
    }

    override fun createEntities(entities: Collection<Role>) = flow {
        emitAll(roleDAO.create(entities))
    }

    @Throws(Exception::class)
    override suspend fun modifyEntities(roles: Collection<Role>) = flow {
        roles.map { role: Role -> saveRole(role) }
                .filterNotNull()
                .onEach { emit(it) }
    }

    override fun getEntities() = flow() {
        emitAll(roleDAO.getEntities())
    }

    override fun getEntitiesIds() = flow<String> {
        emitAll(roleDAO.getEntities().mapNotNull { it.id })
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

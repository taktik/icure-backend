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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.taktik.icure.constants.Permissions
import org.taktik.icure.constants.Roles
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.asynclogic.RoleLogic
import org.taktik.icure.asynclogic.impl.PrincipalLogicImpl
import java.util.*
import java.util.stream.Collectors

@Transactional
@Service
class RoleLogicImpl : PrincipalLogicImpl<Role?>(), RoleLogic {
    private var userDAO: UserDAO? = null
    override fun getRoleByName(name: String): Role {
        return roleDAO.getByName(name)
    }

    override fun getDescendantRoles(roleId: String): Set<Role> {
        val role = getRole(roleId)
        return if (role != null) {
            getDescendantRoles(role, HashSet())
        } else null
    }

    private fun getDescendantRoles(role: Role?, ignoredRoles: MutableSet<Role>): MutableSet<Role> { // Build descendant roles list
        val roles: MutableSet<Role> = HashSet()
        if (role != null) { // Add this role to ignore list
            ignoredRoles.add(role)
            // Process children
            if (role.children != null) {
                for (child in getChildren(role)) {
                    if (child != null) {
                        if (!ignoredRoles.contains(child)) {
                            roles.add(child)
                            roles.addAll(getDescendantRoles(child, ignoredRoles))
                        }
                    }
                }
            }
        }
        return roles
    }

    override fun getRole(id: String): Role {
        return roleDAO[id]
    }

    override fun getUsers(role: Role): Set<User> {
        return userDAO!!.getSet(role.users)
    }

    override fun createDefaultRoleIfNecessary() {
        if (getRoleByName(Roles.DEFAULT_ROLE_NAME) != null) {
            return
        }
        val defaultRole = Role()
        defaultRole.name = Roles.DEFAULT_ROLE_NAME
        defaultRole.permissions = HashSet(Arrays.asList(Permission.granted(Permissions.Type.AUTHENTICATE)))
        saveRole(defaultRole)
    }

    private fun saveRole(role: Role): Role { // Save role
        val savedRole = roleDAO.save(role)
        // Invalidate PermissionSet/Filter from cache for all descendantRoles/Users
        val descendantRoles = getDescendantRoles(savedRole, HashSet())
        descendantRoles.add(savedRole)
        return savedRole
    }

    override fun newRole(role: Role): Role {
        return saveRole(role)
    }

    private fun getChildren(role: Role): Set<Role> {
        return roleDAO.getSet(role.children)
    }

    @Throws(Exception::class)
    override fun createEntities(roles: Collection<Role>, createdRoles: MutableCollection<Role>): Boolean {
        for (role in roles) {
            createdRoles.add(newRole(role))
        }
        return true
    }

    @Throws(Exception::class)
    override fun updateEntities(roles: Collection<Role>): List<Role> {
        return roles.stream().map { role: Role -> saveRole(role) }.collect(Collectors.toList())
    }

    @Throws(Exception::class)
    override fun deleteEntities(roleIds: Collection<String>) {
        roleDAO.removeByIds(roleIds)
    }

    @Throws(Exception::class)
    override fun undeleteEntities(roleIds: Collection<String>) {
        roleDAO.unremoveByIds(roleIds)
    }

    override fun getAllEntities(): List<Role> {
        return roleDAO.all
    }

    override fun getAllEntityIds(): List<String> {
        return roleDAO.all.stream().map { e: Role -> e.id }.collect(Collectors.toList())
    }

    override fun exists(id: String): Boolean {
        return roleDAO.contains(id)
    }

    override fun hasEntities(): Boolean {
        return roleDAO.hasAny()
    }

    override fun getEntity(id: String): Role {
        return getRole(id)
    }

    override fun getPrincipal(roleId: String): Role {
        return getRole(roleId)
    }

    @Autowired
    fun setUserDAO(userDAO: UserDAO?) {
        this.userDAO = userDAO
    }
}

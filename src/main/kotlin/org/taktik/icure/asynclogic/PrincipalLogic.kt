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
package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.security.Permission
import org.taktik.icure.entities.security.Principal

interface PrincipalLogic<P : Principal> {
	suspend fun getPrincipal(principalId: String): P?
	fun getProperties(principalId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<PropertyStub>
	fun getPermissions(principalId: String, virtualHostId: String, includeDirect: Boolean, includeHerited: Boolean, includeDefault: Boolean): Flow<Permission>
	fun getAscendantRoles(principalId: String): Flow<Role>
}

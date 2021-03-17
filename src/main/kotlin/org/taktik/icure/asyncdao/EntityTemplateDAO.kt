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

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.EntityTemplate

interface EntityTemplateDAO: GenericDAO<EntityTemplate> {
    suspend fun getByUserIdTypeDescr(userId: String, type: String, searchString: String?, includeEntities: Boolean?): Flow<EntityTemplate>
    suspend fun getByTypeDescr(type: String, searchString: String?, includeEntities: Boolean?): Flow<EntityTemplate>
    suspend fun getByUserIdTypeKeyword(userId: String?, type: String?, keyword: String?, includeEntities: Boolean?): Flow<EntityTemplate>
    suspend fun getByTypeKeyword(type: String?, keyword: String?, includeEntities: Boolean?): Flow<EntityTemplate>

}

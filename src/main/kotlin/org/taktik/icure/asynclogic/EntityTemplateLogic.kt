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
import org.taktik.icure.entities.EntityTemplate

interface EntityTemplateLogic : EntityPersister<EntityTemplate, String> {
    suspend fun createEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate?

    suspend fun modifyEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate?

    suspend fun getEntityTemplate(id: String): EntityTemplate?
    fun getEntityTemplates(selectedIds: Collection<String>): Flow<EntityTemplate>

    fun listEntityTemplatesBy(userId: String, entityType: String, searchString: String?, includeEntities: Boolean?): Flow<EntityTemplate>

    fun listEntityTemplatesBy(entityType: String, searchString: String?, includeEntities: Boolean?): Flow<EntityTemplate>

    fun listEntityTemplatesByKeyword(userId: String, entityType: String, keyword: String?, includeEntities: Boolean?): Flow<EntityTemplate>

    fun listEntityTemplatesByKeyword(entityType: String, keyword: String?, includeEntities: Boolean?): Flow<EntityTemplate>

}

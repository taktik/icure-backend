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
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.EntityTemplateDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.EntityTemplateLogic
import org.taktik.icure.entities.EntityTemplate
import org.taktik.icure.utils.firstOrNull

@ExperimentalCoroutinesApi
@Service
class EntityTemplateLogicImpl(private val entityTemplateDAO: EntityTemplateDAO,
                              private val sessionLogic: AsyncSessionLogic): GenericLogicImpl<EntityTemplate, EntityTemplateDAO>(sessionLogic), EntityTemplateLogic {

    override suspend fun createEntityTemplate(entityTemplate: EntityTemplate) = fix(entityTemplate) { entityTemplate ->
        val createdEntityTemplates = try {
            createEntities(setOf(entityTemplate))
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid template", e)
        }
        createdEntityTemplates.firstOrNull()
    }

    override suspend fun modifyEntityTemplate(entityTemplate: EntityTemplate) = fix(entityTemplate) { entityTemplate ->
        val entityTemplates = setOf(entityTemplate)
        val updated = try {
            updateEntities(entityTemplates)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid template", e)
        }
        updated.firstOrNull()
    }

    override suspend fun getEntityTemplate(id: String): EntityTemplate? {
        return getEntity(id)
    }

    override fun getEntityTemplates(selectedIds: Collection<String>): Flow<EntityTemplate> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(entityTemplateDAO.getList(dbInstanceUri, groupId, selectedIds))
    }

    override suspend fun findEntityTemplates(userId: String, entityType: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return entityTemplateDAO.getByUserIdTypeDescr(dbInstanceUri, groupId, userId, entityType, searchString, includeEntities)
    }

    override suspend fun findAllEntityTemplates(entityType: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return entityTemplateDAO.getByTypeDescr(dbInstanceUri, groupId, entityType, searchString, includeEntities)
    }

    override fun getGenericDAO(): EntityTemplateDAO {
        return entityTemplateDAO
    }
}

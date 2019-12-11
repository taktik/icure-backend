package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.EntityTemplate

interface EntityTemplateLogic : EntityPersister<EntityTemplate, String> {
    suspend fun createEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate?

    suspend fun modifyEntityTemplate(entityTemplate: EntityTemplate): EntityTemplate?

    suspend fun getEntityTemplate(id: String): EntityTemplate?
    fun getEntityTemplates(selectedIds: Collection<String>): Flow<EntityTemplate>

    suspend fun findEntityTemplates(userId: String, entityType: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate>

    suspend fun findAllEntityTemplates(entityType: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate>
}

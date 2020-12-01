package org.taktik.icure.asyncdao

import org.taktik.icure.entities.EntityTemplate
import java.net.URI

interface EntityTemplateDAO: GenericDAO<EntityTemplate> {
    suspend fun getByUserIdTypeDescr(userId: String, type: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate>

    suspend fun getByTypeDescr(type: String, searchString: String?, includeEntities: Boolean?): List<EntityTemplate>
}

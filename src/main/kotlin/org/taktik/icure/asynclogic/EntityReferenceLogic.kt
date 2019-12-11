package org.taktik.icure.asynclogic

import org.taktik.icure.entities.EntityReference

interface EntityReferenceLogic : EntityPersister<EntityReference, String> {
    suspend fun getLatest(prefix: String): EntityReference?
}

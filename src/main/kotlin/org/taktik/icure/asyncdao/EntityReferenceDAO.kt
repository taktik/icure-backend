package org.taktik.icure.asyncdao

import org.taktik.icure.entities.EntityReference
import java.net.URI

interface EntityReferenceDAO: GenericDAO<EntityReference> {
    suspend fun getLatest(prefix: String): EntityReference?
}

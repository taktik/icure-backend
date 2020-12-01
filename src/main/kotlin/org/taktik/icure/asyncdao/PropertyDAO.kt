package org.taktik.icure.asyncdao

import org.taktik.icure.entities.Property
import java.net.URI

interface PropertyDAO: GenericDAO<Property> {
    suspend fun getByIdentifier(propertyIdentifier: String): Property?

    suspend fun evictFromCache(entity: Property)
    suspend fun putInCache(key: String, entity: Property)
}

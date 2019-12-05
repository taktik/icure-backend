package org.taktik.icure.asyncdao

import org.ektorp.support.View
import org.taktik.icure.entities.Property
import java.net.URI

interface PropertyDAO: GenericDAO<Property> {
    suspend fun getByIdentifier(dbInstanceUrl: URI, groupId: String, propertyIdentifier: String): Property?

    fun evictFromCache(dbInstanceUrl: URI, groupId: String, entity: Property)
    fun putInCache(dbInstanceUrl: URI, groupId: String, key: String, entity: Property)
}

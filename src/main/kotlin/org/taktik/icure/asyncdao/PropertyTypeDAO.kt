package org.taktik.icure.asyncdao

import org.ektorp.support.View
import org.taktik.icure.entities.PropertyType
import java.net.URI

interface PropertyTypeDAO: GenericDAO<PropertyType> {
    @View(name = "by_identifier", map = "function(doc) {\n" +
            "            if (doc.java_type == 'org.taktik.icure.entities.PropertyType' && !doc.deleted && doc.identifier) {\n" +
            "            emit(doc.identifier,doc._id);\n" +
            "}\n" +
            "}")
    suspend fun getByIdentifier(propertyTypeIdentifier: String): PropertyType?

    suspend fun evictFromCache(entity: PropertyType)
    suspend fun putInCache(key: String, entity: PropertyType)
}

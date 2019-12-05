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
    suspend fun getByIdentifier(dbInstanceUrl: URI, groupId: String, propertyTypeIdentifier: String): PropertyType?

    fun evictFromCache(dbInstanceUrl: URI, groupId: String, entity: PropertyType)
    fun putInCache(dbInstanceUrl: URI, groupId: String, key: String, entity: PropertyType)
}

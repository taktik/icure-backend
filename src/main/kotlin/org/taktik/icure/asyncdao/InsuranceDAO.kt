package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.entities.Insurance
import java.net.URI

interface InsuranceDAO: GenericDAO<Insurance> {
    fun listByCode(dbInstanceUrl: URI, groupId: String, code: String): Flow<Insurance>

    fun listByName(dbInstanceUrl: URI, groupId: String, name: String): Flow<Insurance>
}

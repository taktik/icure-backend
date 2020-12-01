package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Insurance
import java.net.URI

interface InsuranceDAO: GenericDAO<Insurance> {
    fun listByCode(code: String): Flow<Insurance>

    fun listByName(name: String): Flow<Insurance>
}

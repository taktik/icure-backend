package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.FrontEndMigration
import java.net.URI

interface FrontEndMigrationDAO: GenericDAO<FrontEndMigration> {
    fun getByUserIdName(userId: String, name: String?): Flow<FrontEndMigration>
}

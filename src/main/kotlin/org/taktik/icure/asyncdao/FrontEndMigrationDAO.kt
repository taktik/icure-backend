package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.entities.FrontEndMigration
import java.net.URI

interface FrontEndMigrationDAO: GenericDAO<FrontEndMigration> {
    fun getByUserIdName(dbInstanceUrl: URI, groupId: String, userId: String, name: String?): Flow<FrontEndMigration>
}

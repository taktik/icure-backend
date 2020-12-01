package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.FrontEndMigrationDAO
import org.taktik.icure.entities.FrontEndMigration

interface FrontEndMigrationLogic : EntityPersister<FrontEndMigration, String> {
    suspend fun createFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration?
    suspend fun deleteFrontEndMigration(frontEndMigrationId: String): DocIdentifier?

    suspend fun getFrontEndMigration(frontEndMigrationId: String): FrontEndMigration?
    fun getFrontEndMigrationByUserIdName(userId: String, name: String?): Flow<FrontEndMigration>

    suspend fun modifyFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration?
    fun getGenericDAO(): FrontEndMigrationDAO
}

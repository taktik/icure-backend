package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.FrontEndMigrationDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.FrontEndMigrationLogic
import org.taktik.icure.entities.FrontEndMigration
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.utils.firstOrNull

@ExperimentalCoroutinesApi
@Service
class FrontEndMigrationLogicImpl(private val frontEndMigrationDAO: FrontEndMigrationDAO,
                                 private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<FrontEndMigration, FrontEndMigrationDAO>(sessionLogic), FrontEndMigrationLogic {

    override suspend fun createFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? {
        return frontEndMigrationDAO.create(frontEndMigration)
    }

    override suspend fun deleteFrontEndMigration(frontEndMigrationId: String): DocIdentifier? {
        return try {
            deleteByIds(setOf(frontEndMigrationId)).firstOrNull()
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getFrontEndMigration(frontEndMigrationId: String): FrontEndMigration? {
        return frontEndMigrationDAO.get(frontEndMigrationId)
    }

    override fun getFrontEndMigrationByUserIdName(userId: String, name: String?): Flow<FrontEndMigration> = flow {
        emitAll(frontEndMigrationDAO.getByUserIdName(userId, name))
    }

    override suspend fun modifyFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? {
        return frontEndMigrationDAO.save(frontEndMigration)
    }

    override fun getGenericDAO(): FrontEndMigrationDAO {
        return frontEndMigrationDAO
    }
}

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.FrontEndMigrationDAO
import org.taktik.icure.asynclogic.AsyncICureSessionLogic
import org.taktik.icure.asynclogic.FrontEndMigrationLogic
import org.taktik.icure.entities.FrontEndMigration
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.utils.firstOrNull

@ExperimentalCoroutinesApi
@Service
class FrontEndMigrationLogicImpl(private val frontEndMigrationDAO: FrontEndMigrationDAO,
                                 private val sessionLogic: AsyncICureSessionLogic) : GenericLogicImpl<FrontEndMigration, FrontEndMigrationDAO>(sessionLogic), FrontEndMigrationLogic {

    override suspend fun createFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return frontEndMigrationDAO.create(dbInstanceUri, groupId, frontEndMigration)
    }

    override suspend fun deleteFrontEndMigration(frontEndMigrationId: String): DocIdentifier? {
        return try {
            deleteByIds(setOf(frontEndMigrationId)).firstOrNull()
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getFrontEndMigration(frontEndMigrationId: String): FrontEndMigration? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return frontEndMigrationDAO.get(dbInstanceUri, groupId, frontEndMigrationId)
    }

    override fun getFrontEndMigrationByUserIdName(userId: String, name: String?): Flow<FrontEndMigration> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(frontEndMigrationDAO.getByUserIdName(dbInstanceUri, groupId, userId, name))
    }

    override suspend fun modifyFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return frontEndMigrationDAO.save(dbInstanceUri, groupId, frontEndMigration)
    }

    override fun getGenericDAO(): FrontEndMigrationDAO {
        return frontEndMigrationDAO
    }
}

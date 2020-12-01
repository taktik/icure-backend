package org.taktik.icure.asynclogic.impl

import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.EntityReferenceDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.entities.EntityReference

@Service
class EntityReferenceLogicImpl(private val entityReferenceDAO: EntityReferenceDAO,
                               private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<EntityReference, EntityReferenceDAO>(sessionLogic), EntityReferenceLogic {

    override suspend fun getLatest(prefix: String): EntityReference? {
        return entityReferenceDAO.getLatest(prefix)
    }

    override fun getGenericDAO(): EntityReferenceDAO {
        return entityReferenceDAO
    }
}

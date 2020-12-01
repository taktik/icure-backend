/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

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

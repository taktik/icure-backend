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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.dao.impl.idgenerators.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.MedicalLocationDAO
import org.taktik.icure.entities.MedicalLocation
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery

@Repository("MedicalLocationDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.MedicalLocation' && !doc.deleted) emit( null, doc._id )}")
class MedicalLocationDAOImpl(couchDbProperties: CouchDbProperties,
                             @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator
) : GenericDAOImpl<MedicalLocation>(couchDbProperties, MedicalLocation::class.java, couchDbDispatcher, idGenerator), MedicalLocationDAO {
    @View(name = "by_post_code", map = "classpath:js/medicallocation/By_post_code_map.js")
    override fun byPostCode(postCode: String): Flow<MedicalLocation> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.queryViewIncludeDocs<String, String, MedicalLocation>(createQuery<MedicalLocation>("by_post_code").includeDocs(true).key(postCode)).map { it.doc }
    }
}

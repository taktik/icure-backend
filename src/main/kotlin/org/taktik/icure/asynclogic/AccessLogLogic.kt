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

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import java.time.Instant

interface AccessLogLogic : EntityPersister<AccessLog, String> {
    suspend fun createAccessLog(accessLog: AccessLog): AccessLog?

    fun deleteAccessLogs(ids: List<String>): Flow<DocIdentifier>
    fun listAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog>

    suspend fun getAccessLog(accessLogId: String): AccessLog?

    fun listAccessLogsBy(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findAccessLogsByUserAfterDate(userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<List<String>>, descending: Boolean): Flow<ViewQueryResultEvent>

    suspend fun modifyAccessLog(accessLog: AccessLog): AccessLog?
    fun getGenericDAO(): AccessLogDAO
}

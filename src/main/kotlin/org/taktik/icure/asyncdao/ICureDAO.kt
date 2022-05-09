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

package org.taktik.icure.asyncdao

import java.net.URI
import org.taktik.couchdb.ReplicatorResponse
import org.taktik.couchdb.entity.ReplicateCommand
import org.taktik.couchdb.entity.Scheduler
import org.taktik.icure.entities.embed.DatabaseSynchronization

interface ICureDAO {
	suspend fun getIndexingStatus(dbInstanceUri: URI): Map<String, Int>
	suspend fun getPendingChanges(dbInstanceUri: URI): Map<DatabaseSynchronization, Long>
	suspend fun replicate(command: ReplicateCommand): ReplicatorResponse
	suspend fun deleteReplicatorDoc(docId: String): ReplicatorResponse
	suspend fun getSchedulerDocs(): Scheduler.Docs
}

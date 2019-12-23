package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.dao.Option
import org.taktik.icure.entities.Group
import java.net.URI

interface GroupDAO {
    fun getAll(): Flow<Group>
    suspend fun save(group: Group): Group?
    fun getList(ids: Flow<String>): Flow<Group>
    fun getAllIds(): Flow<String>
    suspend fun get(id: String, vararg options: Option): Group?
    suspend fun get(id: String, rev: String?, vararg options: Option): Group?
}

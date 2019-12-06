package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.ViewQuery
import org.ektorp.support.View
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.entities.User
import java.net.URI
import java.time.Instant

interface UserDAO : GenericDAO<User>{
	fun getExpiredUsers(dbInstanceUrl: URI, groupId: String, fromExpirationInstant: Instant, toExpirationInstant: Instant): Flow<User>
	fun findByUsername(dbInstanceUrl: URI, groupId: String, searchString: String): Flow<User>
    fun findByEmail(dbInstanceUrl: URI, groupId: String, searchString: String): Flow<User>
    fun listUsers(dbInstanceUrl: URI, groupId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    suspend fun getOnFallback(dbInstanceUrl: URI, groupId: String, userId: String, bypassCache: Boolean): User
    suspend fun findOnFallback(dbInstanceUrl: URI, groupId: String, userId: String, bypassCache: Boolean): User
    fun getUsersByPartialIdOnFallback(dbInstanceUrl: URI, groupId: String, id: String): Flow<User>
    fun findByHcpId(dbInstanceUrl: URI, groupId: String, hcPartyId: String): Flow<User>
    fun findByUsernameOnFallback(dbInstanceUrl: URI, groupId: String, login: String): Flow<User>
    suspend fun getUserOnUserDb(dbInstanceUrl: URI, groupId: String, userId: String, bypassCache: Boolean): User
    suspend fun findUserOnUserDb(dbInstanceUrl: URI, groupId: String, userId: String, bypassCache: Boolean): User
    fun getUsersOnDb(dbInstanceUrl: URI, groupId: String): Flow<User>
    fun evictFromCache(dbInstanceUrl: URI, groupId: String, userIds: Flow<String>)
    suspend fun saveOnFallback(dbInstanceUrl: URI, groupId: String, user: User): User
    suspend fun save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entity: User?): User?
}

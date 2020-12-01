package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import java.net.URI
import java.time.Instant

interface UserDAO : GenericDAO<User>{
	fun getExpiredUsers(fromExpirationInstant: Instant, toExpirationInstant: Instant): Flow<User>
	fun findByUsername(searchString: String): Flow<User>
    fun findByEmail(searchString: String): Flow<User>
    fun listByEmailOnFallbackDb(email: String): Flow<User>
    fun listUsers(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    suspend fun getOnFallback(userId: String, bypassCache: Boolean): User?
    fun getUsersByPartialIdOnFallback(id: String): Flow<User>
    fun findByHcpId(hcPartyId: String): Flow<User>
    fun findByUsernameOnFallback(login: String): Flow<User>
    suspend fun getUserOnUserDb(userId: String, bypassCache: Boolean): User
    suspend fun findUserOnUserDb(userId: String, bypassCache: Boolean): User?
    fun getUsersOnDb(dbInstanceUrl: URI): Flow<User>
    suspend fun evictFromCache(userIds: Flow<String>)
    suspend fun saveOnFallback(user: User): User
    suspend fun save(newEntity: Boolean?, entity: User): User?
}

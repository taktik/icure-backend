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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.exception.DocumentNotFoundException
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.couchdb.update
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI
import java.time.Instant
import java.util.*

@Repository("userDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) emit( null, doc._rev )}")
class UserDAOImpl(couchDbProperties: CouchDbProperties,
                  @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager) : CachedDAOImpl<User>(User::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), UserDAO {
    @View(name = "by_exp_date", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.expirationDate.epochSecond, doc._id)  }}")
    override fun getExpiredUsers(fromExpirationInstant: Instant, toExpirationInstant: Instant): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val users = client.queryViewIncludeDocs<String, String, User>(createQuery<User>("by_exp_date").startKey(fromExpirationInstant.toString()).endKey(toExpirationInstant.toString()).includeDocs(true)).map { it.doc }

        return users.filter { it.expirationDate != null && !it.expirationDate.isBefore(fromExpirationInstant) && !it.expirationDate.isAfter(toExpirationInstant) }
    }

    @View(name = "by_username", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.login, null)}}")
    override fun findByUsername(searchString: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String, User>(createQuery<User>("by_username").includeDocs(true).key(searchString)).mapNotNull { it.doc }
    }

    @View(name = "by_email", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.email, null)}}")
    override fun findByEmail(searchString: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String, User>(createQuery<User>("by_email").includeDocs(true).key(searchString)).mapNotNull { it.doc }
    }

    override fun listByEmailOnFallbackDb(email: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.queryViewIncludeDocsNoValue<String, User>(createQuery<User>("by_email").includeDocs(true).key(email)).mapNotNull { it.doc }
    }

    /**
     * startKey in pagination is the email of the patient.
     */
    @View(name = "allForPagination", map = "map = function (doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) { emit(doc.login, null); }};")
    override fun listUsers(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = pagedViewQuery<User,String>("allForPagination", null, "\ufff0", pagination, false)
        return client.queryView(viewQuery, String::class.java, Nothing::class.java, User::class.java)
    }

    override suspend fun getOnFallback(userId: String, bypassCache: Boolean): User? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val valueWrapper = if (bypassCache) null else getWrapperFromCache(userId)
        if (valueWrapper == null) {
            val user = client.get(userId, User::class.java)
            putInCache(userId, user)
            return user
        }
        return valueWrapper.get() as User
    }

    @View(name = "by_id", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc._id.split(':')[1] || doc._id, null)}}")
    override fun getUsersByPartialIdOnFallback(id: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String, User>(createQuery<User>("by_id").includeDocs(true).key(id)).map { it.doc }
    }

    @View(name = "by_hcp_id", map = "classpath:js/user/by_hcp_id.js")
    override fun findByHcpId(hcPartyId: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String,User>(createQuery<User>("by_hcp_id").key(hcPartyId).includeDocs(true)).map { it.doc }
    }

    override fun findByUsernameOnFallback(login: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String,User>(createQuery<User>("by_username").includeDocs(true).key(login)).map { it.doc }
    }

    override suspend fun getUserOnUserDb(userId: String, bypassCache: Boolean): User {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val value = if (bypassCache) null else getWrapperFromCache(userId)

        if (value == null) {
            val user = client.get(userId, User::class.java)
            putInCache(userId, user)
            if (user == null) {
                throw DocumentNotFoundException(userId)
            }
            return user
        }
        if (value.get() == null) {
            throw DocumentNotFoundException(userId)
        }
        return value.get() as User
    }

    override suspend fun findUserOnUserDb(userId: String, bypassCache: Boolean): User? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val value = if (bypassCache) null else getWrapperFromCache(userId)

        if (value == null) {
            val user = client.get(userId, User::class.java)
            putInCache(userId, user)
            return user
        }
        return value.get() as User?
    }

    override fun getUsersOnDb(dbInstanceUrl: URI): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String, User>(createQuery<User>("all").includeDocs(true)).map { it.doc }
    }

    override suspend fun evictFromCache(userIds: Flow<String>) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        userIds.collect { u ->
            super.evictFromCache(u)
            super.evictFromCache(u)
        }
    }

    override suspend fun saveOnFallback(user: User): User {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.update(user)
    }

    override suspend fun save(newEntity: Boolean?, entity: User): User? {
        return super.save(
                newEntity,
                if (entity.use2fa == true && !entity.applicationTokens.containsKey("ICC"))
                    entity.copy(applicationTokens = entity.applicationTokens + ("ICC" to UUID.randomUUID().toString()))
                else entity
        )
    }
}

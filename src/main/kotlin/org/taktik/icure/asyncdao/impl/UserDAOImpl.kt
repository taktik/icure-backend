/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asyncdao.impl

import com.fasterxml.uuid.Generators
import kotlinx.coroutines.flow.*
import org.ektorp.DocumentNotFoundException
import org.ektorp.ViewQuery
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository
import org.taktik.couchdb.*
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI
import java.time.Instant

@Repository("userDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) emit( null, doc._rev )}")
class UserDAOImpl(@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, @Qualifier("entitiesCacheManager") cacheManager: CacheManager) : CachedDAOImpl<User>(User::class.java, couchDbDispatcher, idGenerator, cacheManager), UserDAO {
    @View(name = "by_exp_date", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.expirationDate.epochSecond,doc)  }}")
    override fun getExpiredUsers(dbInstanceUrl: URI, groupId: String, fromExpirationInstant: Instant, toExpirationInstant: Instant): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val users = client.queryViewIncludeDocs<String, String, User>(createQuery<User>("by_exp_date").startKey(fromExpirationInstant.toString()).endKey(toExpirationInstant.toString()).includeDocs(true)).map { it.doc }

        return users.filter { it.expirationDate != null && !it.expirationDate.isBefore(fromExpirationInstant) && !it.expirationDate.isAfter(toExpirationInstant) }
    }

    @View(name = "by_username", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.login,doc)}}")
    override fun findByUsername(dbInstanceUrl: URI, groupId: String, searchString: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryView<String, User>(createQuery<User>("by_username").includeDocs(false).key(searchString)).mapNotNull { it.value }
    }

    @View(name = "by_email", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.email,doc)}}")
    override fun findByEmail(dbInstanceUrl: URI, groupId: String, searchString: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryView<String, User>(createQuery<User>("by_email").includeDocs(false).key(searchString)).mapNotNull { it.value }
    }

    /**
     * startKey in pagination is the email of the patient.
     */
    @View(name = "allForPagination", map = "map = function (doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) { emit(doc.login, doc._id); }};")
    override fun listUsers(dbInstanceUrl: URI, groupId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = pagedViewQuery<User,String>("allForPagination", if (pagination.startKey != null) pagination.startKey.toString() else "\u0000", "\ufff0", pagination, false)
        return client.queryView(viewQuery, String::class.java, String::class.java, User::class.java)
    }

    override suspend fun getOnFallback(dbInstanceUrl: URI, userId: String, bypassCache: Boolean): User? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val valueWrapper = if (bypassCache) null else getWrapperFromCache(dbInstanceUrl, null, userId)
        if (valueWrapper == null) {
            val user = client.get(userId, User::class.java)
            putInCache(dbInstanceUrl, null, userId, user)
            return user
        }
        return valueWrapper.get() as User
    }

    @View(name = "by_id", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc._id.split(':')[1] || doc._id, null)}}")
    override fun getUsersByPartialIdOnFallback(dbInstanceUrl: URI, id: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, null)

        return client.queryViewIncludeDocsNoValue<String, User>(createQuery<User>("by_id").includeDocs(true).key(id)).map { it.doc }
    }

    @View(name = "by_hcp_id", map = "classpath:js/user/by_hcp_id.js")
    override fun findByHcpId(dbInstanceUrl: URI, groupId: String, hcPartyId: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryViewIncludeDocsNoValue<String,User>(createQuery<User>("by_hcp_id").key(hcPartyId).includeDocs(true)).map { it.doc }
    }

    override fun findByUsernameOnFallback(dbInstanceUrl: URI, login: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, null)

        return client.queryViewIncludeDocsNoValue<String,User>(createQuery<User>("by_username").includeDocs(true).key(login)).map { it.doc }
    }

    override suspend fun getUserOnUserDb(dbInstanceUrl: URI, groupId: String, userId: String, bypassCache: Boolean): User {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val value = if (bypassCache) null else getWrapperFromCache(dbInstanceUrl, groupId, userId)

        if (value == null) {
            val user = client.get(userId, User::class.java)
            putInCache(dbInstanceUrl, groupId, userId, user)
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

    override suspend fun findUserOnUserDb(dbInstanceUrl: URI, groupId: String, userId: String, bypassCache: Boolean): User? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val value = if (bypassCache) null else getWrapperFromCache(dbInstanceUrl, groupId, userId)

        if (value == null) {
            val user = client.get(userId, User::class.java)
            putInCache(dbInstanceUrl, groupId, userId, user)
            return user
        }
        return value.get() as User?
    }

    override fun getUsersOnDb(dbInstanceUrl: URI, groupId: String): Flow<User> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        return client.queryViewIncludeDocsNoValue<String, User>(createQuery<User>("all").includeDocs(true)).map { it.doc }
    }

    override suspend fun evictFromCache(dbInstanceUrl: URI, groupId: String, userIds: Flow<String>) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        userIds.collect { u ->
            super.evictFromCache(dbInstanceUrl, null, u)
            super.evictFromCache(dbInstanceUrl, groupId, u)
        }
    }

    override suspend fun saveOnFallback(dbInstanceUrl: URI, user: User): User {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, null)
        return client.update(user)
    }

    override suspend fun save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entity: User?): User? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        if (entity != null && entity.isUse2fa != null && entity.isUse2fa!! && !entity.applicationTokens.containsKey("ICC")) {
            entity.applicationTokens["ICC"] = Generators.randomBasedGenerator(CryptoUtils.getRandom()).generate().toString()
        }
        return entity?.let { super.save(dbInstanceUrl, groupId, newEntity, it) }
    }


}

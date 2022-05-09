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

import java.net.URI
import java.time.Instant
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.*
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.exception.DocumentNotFoundException
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager

@Repository("userDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) emit( null, doc._rev )}")
class UserDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	@Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager
) : CachedDAOImpl<User>(User::class.java, couchDbProperties, couchDbDispatcher, idGenerator, asyncCacheManager), UserDAO {
	@View(name = "by_exp_date", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted && doc.expirationDate) {emit(doc.expirationDate.epochSecond, doc._id)  }}")
	override fun getExpiredUsers(fromExpirationInstant: Instant, toExpirationInstant: Instant): Flow<User> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val users = client.queryViewIncludeDocs<String, String, User>(createQuery(client, "by_exp_date").startKey(fromExpirationInstant.toString()).endKey(toExpirationInstant.toString()).includeDocs(true)).map { it.doc }

		emitAll(users.filter { it.expirationDate != null && !it.expirationDate.isBefore(fromExpirationInstant) && !it.expirationDate.isAfter(toExpirationInstant) })
	}

	@View(name = "by_username", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.login, null)}}")
	override fun listUsersByUsername(searchString: String): Flow<User> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocsNoValue<String, User>(createQuery(client, "by_username").includeDocs(true).key(searchString)).mapNotNull { it.doc })
	}

	@View(name = "by_email", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted && doc.email) {emit(doc.email, null)}}")
	override fun listUsersByEmail(email: String): Flow<User> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocsNoValue<String, User>(createQuery(client, "by_email").includeDocs(true).key(email)).mapNotNull { it.doc })
	}

	@View(name = "by_phone", map = "classpath:js/user/By_phone.js")
	override fun listUsersByPhone(phone: String): Flow<User> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val fullNormalized = phone.trim().let { if (it.startsWith("+")) "+${it.substring(1).replace(Regex("[^0-9]"), "")}" else it.replace(Regex("[^0-9]"), "") }
		emitAll(client.queryViewIncludeDocsNoValue<String, User>(createQuery(client, "by_phone").includeDocs(true).key(fullNormalized)).mapNotNull { it.doc })
	}

	/**
	 * startKey in pagination is the email of the patient.
	 */
	@View(name = "allForPagination", map = "map = function (doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) { emit(doc.login, null); }};")
	override fun findUsers(pagination: PaginationOffset<String>, skipPatients: Boolean): Flow<ViewQueryResultEvent> = findUsers(pagination, skipPatients, 1f, 0, false)
	fun findUsers(pagination: PaginationOffset<String>, skipPatients: Boolean, extensionFactor: Float, prevTotalCount: Int, isContinuation: Boolean): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val extendedLimit = (pagination.limit * extensionFactor).toInt()
		val viewQuery = pagedViewQuery<User, String>(
			client,
			"allForPagination",
			null,
			"\ufff0",
			pagination.copy(limit = extendedLimit),
			false
		)
		var seenElements = 0
		var sentElements = 0
		var totalCount = 0
		var latestResult: ViewRowWithDoc<*, *, *>? = null
		var skipped = false
		emitAll(
			client.queryView(viewQuery, String::class.java, Nothing::class.java, User::class.java).let { flw ->
				if (!skipPatients) flw else
					flw.filter {
						when (it) {
							is ViewRowWithDoc<*, *, *> -> {
								latestResult = it
								seenElements++
								if (skipped || !isContinuation) {
									if ((it.doc as User).patientId === null && sentElements < pagination.limit) {
										sentElements++
										true
									} else false
								} else {
									skipped = true
									false
								}
							}
							is TotalCount -> {
								totalCount = it.total
								false
							}
							else -> true
						}
					}.onCompletion {
						if ((seenElements >= extendedLimit) && (sentElements < seenElements)) {
							emitAll(
								findUsers(
									pagination.copy(startKey = latestResult?.key as? String, startDocumentId = latestResult?.id, limit = pagination.limit - sentElements),
									true,
									(if (seenElements == 0) extensionFactor * 2 else (seenElements.toFloat() / sentElements)).coerceAtMost(100f),
									totalCount + prevTotalCount,
									true
								)
							)
						} else {
							emit(TotalCount(totalCount + prevTotalCount))
						}
					}
			}
		)
	}

	@View(name = "by_hcp_id", map = "classpath:js/user/by_hcp_id.js")
	override fun listUsersByHcpId(hcPartyId: String): Flow<User> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocsNoValue<String, User>(createQuery(client, "by_hcp_id").key(hcPartyId).includeDocs(true)).map { it.doc })
	}

	@View(name = "by_name_email_phone", map = "classpath:js/user/By_name_email_phone.js")
	override fun listUserIdsByNameEmailPhone(searchString: String): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.queryView<String, Int>(createQuery(client, "by_name_email_phone").startKey(searchString).endKey("$searchString\ufff0").includeDocs(false)).map { it.id })
	}

	override fun findUsersByNameEmailPhone(
		searchString: String,
		pagination: PaginationOffset<String>
	): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val viewQuery = pagedViewQuery<User, String>(client, "by_name_email_phone", searchString, "$searchString\ufff0", pagination, false)
		emitAll(client.queryView(viewQuery, String::class.java, Nothing::class.java, User::class.java))
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

	override fun getUsersOnDb(dbInstanceUrl: URI): Flow<User> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocsNoValue<String, User>(createQuery(client, "all").includeDocs(true)).map { it.doc })
	}

	override suspend fun evictFromCache(userIds: Flow<String>) {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		userIds.collect { u ->
			super.evictFromCache(u)
			super.evictFromCache(u)
		}
	}

	override fun findUsersByIds(userIds: Flow<String>): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.getForPagination(userIds, User::class.java))
	}
}

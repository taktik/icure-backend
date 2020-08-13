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

package org.taktik.icure.dao.impl;

import com.fasterxml.uuid.Generators;
import org.ektorp.ComplexKey;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.support.Filter;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.UserDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.User;
import org.taktik.icure.security.CryptoUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository("userDAO")
@Filter(name = "db_replication_filter", function = "function(doc) { return (doc.java_type == 'org.taktik.icure.entities.User' || doc.java_type == 'org.taktik.icure.entities.HealthcareParty') }")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) emit( null, doc._id )}")
public class UserDAOImpl extends CachedDAOImpl<User> implements UserDAO {

	@Autowired
	public UserDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator, @Qualifier("entitiesCacheManager") CacheManager cacheManager) {
		super(User.class, couchdb, idGenerator, cacheManager);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "by_exp_date", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.expirationDate.epochSecond,doc._id)  }}")
	public List<User> getExpiredUsers(Instant fromExpirationInstant, Instant toExpirationInstant) {
		List<User> users = queryView("by_exp_date", fromExpirationInstant.toString(), toExpirationInstant.toString());
		List<User> result = new ArrayList<>();
		for (User user : users) {
			if (user.getExpirationDate() != null) {
				if (!user.getExpirationDate().isBefore(fromExpirationInstant) && !user.getExpirationDate().isAfter(toExpirationInstant)) {
					result.add(user);
				}
			}
		}
		return result;
	}

	@Override
	@View(name = "by_username", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.login,doc._id)}}")
	public List<User> findByUsername(String searchString) {
		return queryView("by_username", searchString);
	}

	@Override
	@View(name = "by_email", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc.email,doc._id)}}")
	public List<User> findByEmail(String searchString) {
		return queryView("by_email", searchString);
	}

	/**
	 * startKey in pagination is the email of the patient.
	 */
	@Override
	@View(name = "allForPagination", map = "map = function (doc) { if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) { emit(doc.login, doc._id); }};")
	public PaginatedList<User> listUsers(PaginationOffset pagination) {
		return pagedQueryView(
				"allForPagination",
				pagination.getStartKey() != null ? pagination.getStartKey().toString() : "\u0000",
				"\ufff0",
				pagination, false
		);
	}

	@Override
	public User getOnFallback(String userId) {
		Cache.ValueWrapper valueWrapper = cache.get(userId);
		if (valueWrapper == null) {
			User user = ((CouchDbICureConnector) db).getFallbackConnector().find(User.class, userId);
			cache.put(userId, user);
			if (user == null) {
				throw new DocumentNotFoundException(userId);
			}
			return user;
		}
		if (valueWrapper.get() == null) {
			throw new DocumentNotFoundException(userId);
		}
		return (User) valueWrapper.get();
	}

	@Override
	public User findOnFallback(String userId) {
		Cache.ValueWrapper valueWrapper = cache.get(userId);
		if (valueWrapper == null) {
			User res = ((CouchDbICureConnector) db).getFallbackConnector().find(User.class, userId);
			cache.put(userId, res);
			return res;
		}
		return (User) valueWrapper.get();
	}

	@Override
	@View(name = "by_id", map = "function(doc) {  if (doc.java_type == 'org.taktik.icure.entities.User' && !doc.deleted) {emit(doc._id.split(':')[1] || doc._id, null)}}")
	public List<User> getUsersByPartialIdOnFallback(String id) {
		return ((CouchDbICureConnector) db).getFallbackConnector().queryView(createQuery("by_id").includeDocs(true).key(id), type);
	}

	@Override
	@View(name = "by_hcp_id", map = "classpath:js/user/by_hcp_id.js")
	public List<User> findByHcpId(String hcPartyId) {
		return queryView("by_hcp_id", hcPartyId);
	}

	@Override
	public List<User> findByUsernameOnFallback(String login) {
		return ((CouchDbICureConnector) db).getFallbackConnector().queryView(createQuery("by_username").includeDocs(true).key(login), type);
	}

	@Override
	public User getUserOnUserDb(String userId, String groupId, boolean bypassCache) {
		CouchDbICureConnector userDb = ((CouchDbICureConnector) db).getCouchDbICureConnector(groupId);

		String fullId = userDb.getUuid() + ":" + userId;
		Cache.ValueWrapper value = bypassCache ? null : cache.get(fullId);

		if (value == null) {
			User user = userDb.find(User.class, userId);
			cache.put(fullId, user);
			if (user == null) {
				throw new DocumentNotFoundException(userId);
			}
			return user;
		}
		if (value.get() == null) {
			throw new DocumentNotFoundException(userId);
		}
		return (User) value.get();
	}

	@Override
	public User findUserOnUserDb(String userId, String groupId, boolean bypassCache) {
		CouchDbICureConnector userDb = ((CouchDbICureConnector) db).getCouchDbICureConnector(groupId);

		String fullId = userDb.getUuid() + ":" + userId;
		Cache.ValueWrapper value = bypassCache ? null : cache.get(fullId);

		if (value == null) {
			User user = userDb.find(User.class, userId);
			cache.put(fullId, user);
			return user;
		}
		return (User) value.get();
	}

	@Override
	public List<User> getUsersOnDb(String groupId) {
		return ((CouchDbICureConnector) db).getCouchDbICureConnector(groupId).queryView(createQuery("all").includeDocs(true), User.class);
	}

	@Override
	public void evictFromCache(String groupId, List<String> userIds) {
		userIds.forEach(u -> {
			super.evictFromCache(u);
			super.evictFromCache(groupId, u);
		});

		super.evictFromCache(ALL_ENTITIES_CACHE_KEY);
		super.evictFromCache(groupId, ALL_ENTITIES_CACHE_KEY);
	}

	@Override
	public User saveOnFallback(User user) {
		((CouchDbICureConnector) db).getFallbackConnector().update(user);
		return user;
	}

	@Override
	protected User save(Boolean newEntity, User entity) {
		if (entity != null && entity.isUse2fa() != null && entity.isUse2fa() && !entity.getApplicationTokens().containsKey("ICC")) {
			entity.getApplicationTokens().put("ICC", Generators.randomBasedGenerator(CryptoUtils.getRandom()).generate().toString());
		}
		return super.save(newEntity, entity);
	}


}

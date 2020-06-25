/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hazelcast.core.HazelcastInstance
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.dao.replicator.ReplicationManager
import org.taktik.icure.dao.replicator.Replicator
import org.taktik.icure.dao.replicator.UserReplicator
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncHazelCastCacheManager

@Configuration
class CouchDbCloudConfig(val couchDbProperties: CouchDbProperties) {
    @Value("\${icure.couchdb.prefix}")
    private val couchDbPrefix: String? = null

    @Bean
    fun asyncCacheManager(hazelcastInstance: HazelcastInstance) = AsyncHazelCastCacheManager(hazelcastInstance)

    @Bean
    fun sslContextFactory() = SslContextFactory()
    @Bean
    fun userReplicator(couchDbProperties: CouchDbProperties, sslContextFactory: SslContextFactory, userDAO: UserDAO) = UserReplicator(couchDbProperties, sslContextFactory, userDAO)

    @ConditionalOnProperty("icure.sync.global.databases", havingValue = "true", matchIfMissing = true)
    @Bean
    fun replicationManager(hazelcastInstance: HazelcastInstance, sslContextFactory: SslContextFactory, groupDAO: GroupDAO, replicators: List<Replicator>, allDaos: List<GenericDAO<*>>) = ReplicationManager(hazelcastInstance, sslContextFactory, groupDAO, replicators, allDaos)

    @Bean
    fun httpClient() = HttpClient(SslContextFactory.Client()).apply { start() }
    @Bean
    fun patientCouchDbDispatcher(httpClient: HttpClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "patient", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun healthdataCouchDbDispatcher(httpClient: HttpClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "healthdata", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun baseCouchDbDispatcher(httpClient: HttpClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "base", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun configCouchDbDispatcher(httpClient: HttpClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "config", couchDbProperties.username!!, couchDbProperties.password!!)
    @Bean
    fun drugCouchDbDispatcher(httpClient: HttpClient, objectMapper: ObjectMapper) = CouchDbDispatcher(httpClient, objectMapper, "icure", "drug", couchDbProperties.username!!, couchDbProperties.password!!)
}

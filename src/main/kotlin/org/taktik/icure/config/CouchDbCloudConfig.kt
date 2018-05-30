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

import org.ektorp.CouchDbInstance
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.ektorp.spring.HttpClientFactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.taktik.icure.dao.impl.ektorp.StdCouchDbICureConnector
import org.taktik.icure.dao.impl.ektorp.StdUserDependentCouchDbICureConnector
import org.taktik.icure.properties.CouchDbProperties

@Configuration
class CouchDbCloudConfig(val couchDbProperties: CouchDbProperties) {
    @Value("\${icure.couchdb.prefix}")
    private val couchDbPrefix: String? = null

    @Bean fun couchdbInstance() = StdCouchDbInstance(StdHttpClient.Builder()
            .maxConnections(couchDbProperties.maxConnections)
            .socketTimeout(couchDbProperties.socketTimeout)
            .username(couchDbProperties.username)
            .password(couchDbProperties.password)
            .url(couchDbProperties.url)
            .build())

    @Bean fun couchdbConfig(couchdbInstance:CouchDbInstance) = StdCouchDbICureConnector("icure-config", couchdbInstance)
    @Bean fun couchdbBase(couchdbInstance:CouchDbInstance) = StdUserDependentCouchDbICureConnector("$couchDbPrefix-base", couchdbInstance, true)
    @Bean fun couchdbPatient(couchdbInstance:CouchDbInstance) = StdUserDependentCouchDbICureConnector("$couchDbPrefix-patient", couchdbInstance, true)
    @Bean fun couchdbHealthdata(couchdbInstance:CouchDbInstance) = StdUserDependentCouchDbICureConnector("$couchDbPrefix-healthdata", couchdbInstance, true)
}

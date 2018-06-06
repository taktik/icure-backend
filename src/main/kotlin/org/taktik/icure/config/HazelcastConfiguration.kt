/*
 *
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of FreeHealthConnector.
 *
 * FreeHealthConnector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation.
 *
 * FreeHealthConnector is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with FreeHealthConnector.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.taktik.icure.config

import com.hazelcast.config.Config
import com.hazelcast.config.MapAttributeConfig
import com.hazelcast.config.MaxSizeConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession
import org.springframework.session.hazelcast.HazelcastSessionRepository
import com.hazelcast.config.MapIndexConfig
import com.hazelcast.spring.context.SpringManagedContext
import org.springframework.session.hazelcast.PrincipalNameExtractor


@Suppress("UsePropertyAccessSyntax")
@Configuration
@EnableHazelcastHttpSession
class HazelcastConfiguration() {

    @Bean fun managedContext() = SpringManagedContext()

    @Bean
    fun hazelcastConfig(managedContext: SpringManagedContext): Config = Config().apply {
        this.setManagedContext(managedContext)
            .getMapConfig("spring:session:sessions")
            .addMapAttributeConfig(MapAttributeConfig()
                                       .setName(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
                                       .setExtractor(PrincipalNameExtractor::class.java.name))
            .addMapIndexConfig(MapIndexConfig(
                HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false))

        this.getMapConfig("icure:jobs:replicators").setTimeToLiveSeconds(12*3600).setMaxSizeConfig(MaxSizeConfig(100000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
    }
}

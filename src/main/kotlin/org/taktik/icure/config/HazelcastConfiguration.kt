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
import com.hazelcast.config.DiscoveryStrategyConfig
import com.hazelcast.config.MapAttributeConfig
import com.hazelcast.config.MaxSizeConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession
import org.springframework.session.hazelcast.HazelcastSessionRepository
import com.hazelcast.config.MapIndexConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.spring.context.SpringManagedContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.session.hazelcast.PrincipalNameExtractor

@Suppress("UsePropertyAccessSyntax")
@Configuration
@EnableHazelcastHttpSession
class HazelcastConfiguration {

    @Value("\${hazelcast.kubernetes.service:#{null}}")
    var kubernetesServiceName: String? = null

    @Value("\${hazelcast.kubernetes.namespace:#{null}}")
    var kubernetesNamespace: String? = null

    @Autowired(required = false)
    var configurers: List<HazelcastInstanceConfigurer>? = null

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("hazelcast")
    fun hazelcastConfig(): Config = Config()

    @Bean
    @ConditionalOnMissingBean
    fun hazelcastInstance(configuration: Config): HazelcastInstance {
        if (kubernetesNamespace != null && kubernetesServiceName != null) {
            configuration.networkConfig.join.multicastConfig.isEnabled = false
            configuration.networkConfig.join.tcpIpConfig.isEnabled = false

            configuration.networkConfig.join.discoveryConfig.discoveryStrategyConfigs.add(DiscoveryStrategyConfig("com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy").apply {
                addProperty("namespace", kubernetesNamespace)
                addProperty("service-name", kubernetesServiceName)
            })
        }
        configurers?.forEach { it.configure(configuration) }
        return Hazelcast.newHazelcastInstance(configuration)
    }

    @Configuration
    @ConditionalOnClass(name = ["com.hazelcast.spring.context.SpringManagedContext"])
    @ConditionalOnProperty("taktik.boot.hazelcast.springAware.enabled", havingValue = "true", matchIfMissing = true)
    class HazelcastSpringAutoConfiguration {
        @Bean
        fun springManagedContext() = SpringManagedContext()

        @Bean
        fun springManagedContextConfigurer() = object : HazelcastInstanceConfigurer {
            override fun configure(config: Config) {
                config.managedContext = springManagedContext()
            }
        }

        @Bean
        fun springSessionConfigurer() = object : HazelcastInstanceConfigurer {
            override fun configure(config: Config) {
                config.getMapConfig("spring:session:sessions")
                    .addMapAttributeConfig(MapAttributeConfig()
                                               .setName(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
                                               .setExtractor(PrincipalNameExtractor::class.java.name))
                    .addMapIndexConfig(MapIndexConfig(
                        HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false))
            }
        }

        @Bean
        fun replicatorJobsConfigurer() = object : HazelcastInstanceConfigurer {
            override fun configure(config: Config) {
                config.getMapConfig("icure:jobs:replicators").setTimeToLiveSeconds(12*3600).setMaxSizeConfig(MaxSizeConfig(100000, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
            }
        }
    }
}

interface HazelcastInstanceConfigurer {
    fun configure(config: Config)
}

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

import com.hazelcast.config.*
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.kubernetes.KubernetesProperties
import com.hazelcast.spi.properties.ClusterProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


// TODO SH later: cleanup if ok
@Suppress("UsePropertyAccessSyntax")
@Configuration
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

    fun hazelcastConfig(): Config = Config().apply {
        addMapConfig(MapConfig("org.taktik.icure.entities.HealthcareParty").apply {
            timeToLiveSeconds = 5 * 60
            evictionConfig = EvictionConfig().apply {
                maxSizePolicy = MaxSizePolicy.FREE_HEAP_SIZE
                size = 256
                evictionPolicy = EvictionPolicy.LRU
            }
        })
        addMapConfig(MapConfig("org.taktik.icure.entities.User").apply {
            timeToLiveSeconds = 15 * 60
            evictionConfig = EvictionConfig().apply {
                maxSizePolicy = MaxSizePolicy.FREE_HEAP_SIZE
                size = 256
                evictionPolicy = EvictionPolicy.LRU
            }
        })
        addMapConfig(MapConfig("org.taktik.icure.entities.*").apply {
            timeToLiveSeconds = 12 * 3600
            evictionConfig = EvictionConfig().apply {
                maxSizePolicy = MaxSizePolicy.FREE_HEAP_SIZE
                size = 256
                evictionPolicy = EvictionPolicy.LRU
            }
        })

    }

    @Bean("hazelcast")
    @ConditionalOnMissingBean
    fun hazelcastInstance(configuration: Config): HazelcastInstance {
        if (kubernetesNamespace != null && kubernetesServiceName != null) {
            configuration.setProperty(ClusterProperty.DISCOVERY_SPI_ENABLED.name, "true")
            configuration.networkConfig.join.multicastConfig.isEnabled = false
            configuration.networkConfig.join.tcpIpConfig.isEnabled = false
            configuration.networkConfig.join.awsConfig.isEnabled = false
            configuration.networkConfig.join.discoveryConfig.discoveryStrategyConfigs.add(DiscoveryStrategyConfig("com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy").apply {
                addProperty(KubernetesProperties.RESOLVE_NOT_READY_ADDRESSES.key(), "true")
                addProperty("namespace", kubernetesNamespace)
                addProperty("service-name", kubernetesServiceName)
            })
        }
        configurers?.forEach { it.configure(configuration) }
        return Hazelcast.newHazelcastInstance(configuration)
    }

    @Bean
    fun replicatorJobsConfigurer() = object : HazelcastInstanceConfigurer {
        override fun configure(config: Config) {
            config.getMapConfig("icure:jobs:replicators").setTimeToLiveSeconds(12 * 3600).setEvictionConfig(EvictionConfig().apply {
                maxSizePolicy = MaxSizePolicy.FREE_HEAP_SIZE
                size = 256
                evictionPolicy = EvictionPolicy.LRU
            })
        }
    }

}

interface HazelcastInstanceConfigurer {
    fun configure(config: Config)
}

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

package org.taktik.icure

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.entities.embed.*
import org.taktik.icure.services.external.http.WebSocketServlet

@SpringBootApplication(scanBasePackages = [
    "org.springframework.boot.autoconfigure.aop",
    "org.springframework.boot.autoconfigure.context",
    "org.springframework.boot.autoconfigure.dao",
    //"org.springframework.boot.autoconfigure.jackson", TODO SH later: can remove this?
    //"org.springframework.boot.autoconfigure.jersey",
    "org.springframework.boot.autoconfigure.validation",
    "org.springframework.boot.autoconfigure.websocket",
    "org.taktik.icure.config",
    "org.taktik.icure.asyncdao",
    "org.taktik.icure.asynclogic",
    "org.taktik.icure.be.ehealth.logic",
    "org.taktik.icure.be.drugs.logic",
    "org.taktik.icure.be.format.logic",
    "org.taktik.icure.be.samv2.logic",
    "org.taktik.icure.properties",
    "org.taktik.icure.services"
], exclude = [
    FreeMarkerAutoConfiguration::class,
    CacheAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    JndiDataSourceAutoConfiguration::class
])
@PropertySource("classpath:icure-default.properties")
class ICureBackendApplication {
    private val log = LoggerFactory.getLogger(this.javaClass)


    @Bean
    fun initializer(webSocketServlet: WebSocketServlet) = ServletContextInitializer {
        val webSocketServletReg = it.addServlet("webSocketServlet", webSocketServlet)
        webSocketServletReg.setLoadOnStartup(1)
        webSocketServletReg.addMapping("/ws/*")
    }

    @Bean
    fun performStartupTasks(@Qualifier("threadPoolTaskExecutor") taskExecutor: TaskExecutor, taskScheduler: TaskScheduler, iCureLogic: ICureLogic, codeLogic: CodeLogic, propertyLogic: PropertyLogic, allDaos: List<GenericDAO<*>>) = ApplicationRunner {
        //Check that core types have corresponding codes
        log.info("icure (" + iCureLogic.getVersion() + ") is initialised")

        taskExecutor.execute {
            listOf(AddressType::class.java, DocumentType::class.java, DocumentStatus::class.java,
                   Gender::class.java, InsuranceStatus::class.java, PartnershipStatus::class.java, PartnershipType::class.java, PaymentType::class.java,
                   PersonalStatus::class.java, TelecomType::class.java, Confidentiality::class.java, Visibility::class.java).forEach({ codeLogic.importCodesFromEnum(it) })
        }

        taskExecutor.execute {
            val resolver = PathMatchingResourcePatternResolver(javaClass.classLoader)
            resolver.getResources("classpath*:/org/taktik/icure/db/codes/**.xml").forEach {
                val md5 = it.filename!!.replace(Regex(".+\\.([0-9a-f]{20}[0-9a-f]+)\\.xml"), "$1")
                codeLogic.importCodesFromXml(md5, it.filename!!.replace(Regex("(.+)\\.[0-9a-f]{20}[0-9a-f]+\\.xml"), "$1"), it.inputStream)
            }
        }

        log.info("icure (" + iCureLogic.getVersion() + ") is started")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(ICureBackendApplication::class.java, *args)
}

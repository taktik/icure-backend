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

import io.swagger.jaxrs.config.BeanConfig
import io.swagger.jaxrs.listing.ApiListingResource
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.taktik.icure.services.external.rest.handlers.GsonMessageBodyHandler
import org.taktik.icure.services.external.rest.mappings.ObjectMapperProvider
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade
import javax.ws.rs.ApplicationPath

@ApplicationPath("/rest/v1")
@Component
class JerseyConfig(endpoints: List<OpenApiFacade>, @Value("\${server.port}") val serverPort : Int) : ResourceConfig(MultiPartFeature::class.java, ObjectMapperProvider::class.java, GsonMessageBodyHandler::class.java) {
	init {
		registerEndpoints(endpoints)
		configureSwagger()
	}

	private fun configureSwagger() {
		register(ApiListingResource::class.java)
		val beanConfig = BeanConfig()
		beanConfig.version = "1.0.2"
		beanConfig.schemes = arrayOf("https","http")
		beanConfig.basePath = "/rest/v1/"
		beanConfig.resourcePackage = "org.taktik.icure.services.external.rest.v1.facade,org.taktik.icure.services.external.rest.v1.dto"
		beanConfig.prettyPrint = true
		beanConfig.scan = true
	}

	private fun registerEndpoints(endpoints: List<OpenApiFacade>) {
		endpoints.forEach {
			register(it)
		}
	}
}

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

package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@SwaggerDefinition(
		schemes =  { SwaggerDefinition.Scheme.HTTPS, SwaggerDefinition.Scheme.HTTP },
		tags = {
				@Tag(name = "accesslog", description = "Access logs base API"),
				@Tag(name = "code", description = "Codes CRUD and advanced API"),
				@Tag(name = "contact", description = "Contacts CRUD and advanced API"),
				@Tag(name = "document", description = "Documents CRUD and advanced API"),
				@Tag(name = "entitytemplate", description = "Entity templates CRUD and advanced API"),
				@Tag(name = "doctemplate", description = "Entity templates CRUD and advanced API"),
				@Tag(name = "filter", description = "Entity templates CRUD and advanced API"),
				@Tag(name = "form", description = "Forms CRUD and advanced API"),
				@Tag(name = "generic", description = "iCure generic actions API"),
				@Tag(name = "group", description = "Practice groups API"),
				@Tag(name = "hcparty", description = "Healthcare parties CRUD and advanced API"),
				@Tag(name = "helement", description = "Health elements CRUD and advanced API"),
				@Tag(name = "icure", description = "iCure application basic API"),
				@Tag(name = "insurance", description = "Insurances CRUD and advanced API"),
				@Tag(name = "invoice", description = "Invoices CRUD and advanced API"),
				@Tag(name = "auth", description = "Authentification API"),
				@Tag(name = "message", description = "Messages CRUD and advanced API"),
				@Tag(name = "patient", description = "Patients CRUD and advanced API"),
				@Tag(name = "replication", description = "Replication API"),
				@Tag(name = "tarification", description = "Tarifications CRUD and advanced API"),
				@Tag(name = "technicaladmin", description = "Technical internal API"),
				@Tag(name = "user", description = "Users CRUD and advanced API"),
				@Tag(name = "be_drugs", description = "API for belgian Drugs service"),
				@Tag(name = "be_mikrono", description = "API for belgian Mikrono service"),
				@Tag(name = "be_progenda", description = "API for belgian Progenda service"),
				@Tag(name = "be_kmehr", description = "API for belgian Kmehr service"),
				@Tag(name = "be_result_import", description = "API for belgian Result_import service"),
				@Tag(name = "be_result_export", description = "API for belgian Result_export service")
		})
public class OpenApiDefinitionTags {
}

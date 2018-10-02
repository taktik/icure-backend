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

package org.taktik.icure.utils;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

/**
 * A series of utility methods to simplify REST responses generation.
 */
public class ResponseUtils {

	public static Response badRequest(String message) {
		return status(BAD_REQUEST).type(TEXT_PLAIN_TYPE).entity(message).build();
	}

	public static Response internalServerError(String message) {
		return status(INTERNAL_SERVER_ERROR).type(TEXT_PLAIN_TYPE).entity(message).build();
	}

	public static Response notFound(String message) {
		return status(NOT_FOUND).type(TEXT_PLAIN_TYPE).entity(message).build();
	}

	public static Response ok() {
		return Response.ok().build();
	}

	public static Response ok(Object entity) {
		return Response.ok().entity(entity).build();
	}
	public static Response ok(Object entity,String mime) {
		return Response.ok(entity,mime).build();
	}
}

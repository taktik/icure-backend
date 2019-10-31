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

package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.logic.MainLogic
import java.io.Serializable
import java.lang.reflect.InvocationTargetException

@RestController
@RequestMapping("/rest/v1/generic")
@Api(tags = ["generic"])
class GenericController(internal val mapper: MapperFacade,
                        internal val mainLogic: MainLogic) {

    @ApiOperation(nickname = "listEnum", value = "List enum values")
    @GetMapping("/enum/{className}")
    fun listEnum(@PathVariable className: String): List<String> {
        require(className.startsWith("org.taktik.icure.services.external.rest.v1.dto")) { "Invalid package" }
        require(className.matches("[a-zA-Z0-9.]+".toRegex())) { "Invalid class name" }

        try {
            return (Class.forName(className).getMethod("values").invoke(null) as Array<Enum<*>>)
                    .map { it.name }
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException("Invalid class name")
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException("Invalid class name")
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("Invalid class name")
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException("Invalid class name")
        }
    }

    @ApiOperation(nickname = "deleteDoc", value = "Delete docs", notes = "Delete docs based on ID.")
    @DeleteMapping("/doc/{className}/{ids}")
    fun deleteDoc(@PathVariable className: String, @PathVariable ids: String) {
        var prefixedClassName = className
        var c: Class<Serializable>? = null
        if (!prefixedClassName.startsWith("org.taktik.icure.entities.")) {
            prefixedClassName = "org.taktik.icure.entities.$prefixedClassName"
        }
        try {
            c = Class.forName(prefixedClassName) as Class<Serializable>
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException(e)
        }

        try {
            mainLogic.deleteEntities(c, String::class.java, ids.split(',').toSet())
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The documents could not be deleted")
        }
    }

    @ApiOperation(nickname = "getDoc", value = "Get a doc", notes = "Get a doc based on ID.")
    @GetMapping("/doc/{className}/{id}")
    fun getDoc(@PathVariable className: String, @PathVariable id: String): Serializable { // StoredDto?
        var prefixedClassName = className
        var dtoClassName: String

        if (prefixedClassName.startsWith("org.taktik.icure.entities.")) {
            dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + prefixedClassName.substring(26) + "Dto"
        } else if (!prefixedClassName.contains(".")) {
            prefixedClassName = "org.taktik.icure.entities.$prefixedClassName"
            dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + prefixedClassName + "Dto"
        } else {
            throw IllegalArgumentException("Bad class name")
        }

        try {
            Class.forName(dtoClassName)
        } catch (e: ClassNotFoundException) {
            dtoClassName = dtoClassName.replace("Dto$".toRegex(), "")
        }

        try {
            val c = Class.forName(prefixedClassName) as Class<Serializable>
            val dtoC = Class.forName(dtoClassName) as Class<Serializable>
            val r = mainLogic.get(c, id)

            val succeed = r != null
            return if (succeed) {
                mapper.map(r, dtoC)
            } else {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "The document does not exist")
            }
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException(e)
        }
    }

    @ApiOperation(nickname = "listDocs", value = "Get a doc", notes = "List docs of a class.")
    @GetMapping("/doc/{className}")
    fun listDocs(@PathVariable className: String): List<Serializable> {
        var prefixedClassName = className
        var dtoClassName: String

        if (prefixedClassName.startsWith("org.taktik.icure.entities.")) {
            dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + prefixedClassName.substring(26) + "Dto"
        } else if (!prefixedClassName.contains(".")) {
            prefixedClassName = "org.taktik.icure.entities.$prefixedClassName"
            dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + prefixedClassName + "Dto"
        } else {
            throw IllegalArgumentException("Bad class name")
        }

        try {
            Class.forName(dtoClassName)
        } catch (e: ClassNotFoundException) {
            dtoClassName = dtoClassName.replace("Dto$".toRegex(), "")
        }

        try {
            val c = Class.forName(prefixedClassName) as Class<Serializable>
            val dtoC = Class.forName(dtoClassName) as Class<Serializable>

            val r = mainLogic.getEntities(c, null, 0, 1000, null)

            val succeed = r != null
            return if (succeed) {
                r.map { mapper.map(it, dtoC) }
            } else {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The document does not exist")
            }

        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException(e)
        }
    }
}

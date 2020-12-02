/*
 *  iCure Data Stack. Copyright (c) 2020  aduchate
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.couchdb.support

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.IOUtils
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.annotation.Views
import org.taktik.couchdb.util.Exceptions
import org.taktik.couchdb.util.Predicate
import org.taktik.couchdb.util.ReflectionUtils
import java.io.FileNotFoundException
import java.util.HashMap

class SimpleViewGenerator {

    fun generateViews(
            repository: Any,
    ): Map<String, org.taktik.couchdb.entity.View> {
        val views: MutableMap<String, org.taktik.couchdb.entity.View> = HashMap()
        val repositoryClass: Class<*> = repository.javaClass
        createDeclaredViews(views, repositoryClass)
        return views
    }

    private fun createDeclaredViews(views: MutableMap<String, org.taktik.couchdb.entity.View>, klass: Class<*>) {
        ReflectionUtils.eachAnnotation(klass, Views::class.java, object : Predicate<Views> {
            override fun apply(input: Views): Boolean {
                for (v in input.value) {
                    addView(views, v, klass)
                }
                return true
            }
        })

        ReflectionUtils.eachAnnotation(klass, View::class.java, object : Predicate<View> {
            override fun apply(input: View): Boolean {
                addView(views, input, klass)
                return true
            }
        })
    }

    private fun addView(
            views: MutableMap<String, org.taktik.couchdb.entity.View>, input: View,
            repositoryClass: Class<*>,
    ) {
        if (input.file.isNotEmpty()) {
            views[input.name] = loadViewFromFile(views, input, repositoryClass)
        } else if (shouldLoadFunctionFromClassPath(input.map)
                || shouldLoadFunctionFromClassPath(input.reduce)) {
            views[input.name] = loadViewFromFile(input, repositoryClass)
        } else {
            views[input.name] = if (input.reduce.isNotEmpty())
                org.taktik.couchdb.entity.View(input.map, input.reduce)
            else org.taktik.couchdb.entity.View(input.map)
        }
    }

    fun shouldLoadFunctionFromClassPath(function: String?): Boolean {
        return function != null && function.startsWith("classpath:")
    }

    private fun loadViewFromFile(
            input: View,
            repositoryClass: Class<*>,
    ): org.taktik.couchdb.entity.View {
        val mapPath: String = input.map
        val map: String = if (shouldLoadFunctionFromClassPath(mapPath)) {
            loadResourceFromClasspath(repositoryClass, mapPath.substring(10))
        } else {
            mapPath
        }

        val reducePath: String = input.reduce
        val reduce: String? = if (shouldLoadFunctionFromClassPath(reducePath)) {
            loadResourceFromClasspath(repositoryClass, reducePath.substring(10))
        } else {
            if (reducePath.isNotEmpty()) reducePath else null
        }
        return org.taktik.couchdb.entity.View(map, reduce)
    }

    private fun loadResourceFromClasspath(
            repositoryClass: Class<*>,
            path: String,
    ): String {
        return try {
            val `in` = repositoryClass.getResourceAsStream(path)
                    ?: throw FileNotFoundException(
                            "Could not load view file with path: $path")
            IOUtils.toString(`in`, "UTF-8")
        } catch (e: Exception) {
            throw Exceptions.propagate(e)
        }
    }

    private fun loadViewFromFile(
            views: Map<String, org.taktik.couchdb.entity.View?>, input: View,
            repositoryClass: Class<*>,
    ): org.taktik.couchdb.entity.View {
        return try {
            val json = loadResourceFromClasspath(repositoryClass,
                    input.file)
            ObjectMapper().readValue(json.replace("\n".toRegex(), ""),
                    org.taktik.couchdb.entity.View::class.java)
        } catch (e: Exception) {
            throw Exceptions.propagate(e)
        }
    }
}

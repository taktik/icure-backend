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

import org.apache.commons.io.IOUtils
import org.ektorp.util.Assert
import org.taktik.couchdb.annotation.Filter
import org.taktik.couchdb.annotation.Filters
import org.taktik.couchdb.annotation.ListFunction
import org.taktik.couchdb.annotation.Lists
import org.taktik.couchdb.annotation.ShowFunction
import org.taktik.couchdb.annotation.Shows
import org.taktik.couchdb.annotation.UpdateHandler
import org.taktik.couchdb.annotation.UpdateHandlers
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.util.Exceptions
import org.taktik.couchdb.util.Predicate
import org.taktik.couchdb.util.ReflectionUtils
import java.io.FileNotFoundException

/**
 *
 * @author henrik lundgren
 */
class StdDesignDocumentFactory : DesignDocumentFactory {
    var viewGenerator = SimpleViewGenerator()

    /*
     * (non-Javadoc)
     *
     * @see org.ektorp.support.DesignDocumentFactory#generateFrom(java.lang.Object)
     */
    override fun generateFrom(id: String, metaDataSource: Any): DesignDocument {
        val metaDataClass: Class<*> = metaDataSource.javaClass
        return DesignDocument(
                id = id,
                views = viewGenerator.generateViews(metaDataSource),
                lists = createListFunctions(metaDataClass),
                shows = createShowFunctions(metaDataClass),
                filters = createFilterFunctions(metaDataClass),
                updateHandlers = createUpdateHandlerFunctions(metaDataClass)
        )
    }

    private fun createFilterFunctions(metaDataClass: Class<*>): Map<String, String> {
        val shows: MutableMap<String, String> = HashMap()

        ReflectionUtils.eachAnnotation(metaDataClass, Filter::class.java, object : Predicate<Filter> {
            override fun apply(input: Filter): Boolean {
                shows[input.name] = resolveFilterFunction(input, metaDataClass)
                return true
            }
        })


        ReflectionUtils.eachAnnotation(metaDataClass, Filters::class.java, object : Predicate<Filters> {
            override fun apply(input: Filters): Boolean {
                for (sf in input.value) {
                    shows[sf.name] = resolveFilterFunction(sf, metaDataClass)
                }
                return true
            }
        })

        return shows
    }

    private fun createUpdateHandlerFunctions(metaDataClass: Class<*>): Map<String, String> {
        val updateHandlers: MutableMap<String, String> = HashMap()

        ReflectionUtils.eachAnnotation(metaDataClass, UpdateHandler::class.java, object : Predicate<UpdateHandler> {
            override fun apply(input: UpdateHandler): Boolean {
                updateHandlers[input.name] = resolveUpdateHandlerFunction(input, metaDataClass)
                return true
            }
        })

        ReflectionUtils.eachAnnotation(metaDataClass, UpdateHandlers::class.java, object : Predicate<UpdateHandlers> {
            override fun apply(input: UpdateHandlers): Boolean {
                for (sf in input.value) {
                    updateHandlers[sf.name] = resolveUpdateHandlerFunction(sf, metaDataClass)
                }
                return true
            }
        })

        return updateHandlers
    }

    private fun createShowFunctions(metaDataClass: Class<*>): Map<String, String> {
        val shows: MutableMap<String, String> = HashMap()

        ReflectionUtils.eachAnnotation(metaDataClass, ShowFunction::class.java, object : Predicate<ShowFunction> {
            override fun apply(input: ShowFunction): Boolean {
                shows[input.name] = resolveShowFunction(input, metaDataClass)
                return true
            }
        })

        ReflectionUtils.eachAnnotation(metaDataClass, Shows::class.java, object : Predicate<Shows> {
            override fun apply(input: Shows): Boolean {
                for (sf in input.value) {
                    shows[sf.name] = resolveShowFunction(sf, metaDataClass)
                }
                return true
            }
        })

        return shows
    }

    private fun createListFunctions(metaDataClass: Class<*>): Map<String, String> {
        val lists: MutableMap<String, String> = HashMap()

        ReflectionUtils.eachAnnotation(metaDataClass, ListFunction::class.java, object : Predicate<ListFunction> {
            override fun apply(input: ListFunction): Boolean {
                lists[input.name] = resolveListFunction(input, metaDataClass)
                return true
            }
        })

        ReflectionUtils.eachAnnotation(metaDataClass, Lists::class.java, object : Predicate<Lists> {
            override fun apply(input: Lists): Boolean {
                for (lf in input.value) {
                    lists[lf.name] = resolveListFunction(lf, metaDataClass)
                }
                return true
            }
        })

        return lists
    }

    private fun resolveFilterFunction(
            input: Filter,
            metaDataClass: Class<*>,
    ): String {
        if (input.file.isNotEmpty()) {
            return loadFromFile(metaDataClass, input.file)
        }
        Assert.hasText(input.function, "Filter must either have file or function value set")
        return input.function
    }

    private fun resolveUpdateHandlerFunction(
            input: UpdateHandler,
            metaDataClass: Class<*>,
    ): String {
        if (input.file.isNotEmpty()) {
            return loadFromFile(metaDataClass, input.file)
        }
        Assert.hasText(input.function, "UpdateHandler must either have file or function value set")
        return input.function
    }

    private fun resolveListFunction(
            input: ListFunction,
            metaDataClass: Class<*>,
    ): String {
        if (input.file.isNotEmpty()) {
            return loadFromFile(metaDataClass, input.file)
        }
        Assert.hasText(input.function, "ListFunction must either have file or function value set")
        return input.function
    }

    private fun resolveShowFunction(
            input: ShowFunction,
            metaDataClass: Class<*>,
    ): String {
        if (input.file.isNotEmpty()) {
            return loadFromFile(metaDataClass, input.file)
        }
        Assert.hasText(input.function, "ShowFunction must either have file or function value set")
        return input.function
    }

    private fun loadFromFile(metaDataClass: Class<*>, file: String): String {
        return try {
            val `in` = metaDataClass.getResourceAsStream(file)
                    ?: throw FileNotFoundException("Could not load file with path: $file")
            IOUtils.toString(`in`, "UTF-8")
        } catch (e: Exception) {
            throw Exceptions.propagate(e)
        }
    }
}

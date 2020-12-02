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

package org.taktik.couchdb.annotation

/**
 * Annotation for defining views embedded in repositories.
 * @author henrik lundgren
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class View(
        /**
         * The name of the view
         * @return
         */
        val name: String,
        /**
         * Map function or path to function.
         *
         *
         * This value may be a string of code to use for the function.
         * Alternatively, the string may specify a file to load for
         * the function by starting the string with *classpath:*.
         * The rest of the string then represents a relative path to
         * the function.
         * @return
         */
        val map: String = "",
        /**
         * Reduce function or path to function.
         *
         *
         * This value may be a string of code to use for the function.
         * Alternatively, the string may specify a file to load for
         * the function by starting the string with *classpath:*.
         * The rest of the string then represents a relative path to
         * the function.
         * @return
         */
        val reduce: String = "",
        /**
         * Non-trivial views are best stored in a separate files.
         *
         * By specifying the file parameter a view definition can be loaded from the classpath.
         * The path is relative to the class annotated by this annotation.
         *
         * If the file complicated_view.json is in the same directory as the repository this
         * parameter should be set to "complicated_view.json".
         *
         * The file must be a valid json document:
         *
         * {
         * "map": "function(doc) { much javascript here }",
         * // the reduce function is optional
         * "reduce": "function(keys, values) { ... }"
         * }
         *
         * @return
         */
        val file: String = "")

/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
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

package org.taktik.couchdb.util

import org.ektorp.util.Predicate
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Method

class ReflectionUtils {

    companion object {

        fun eachField(clazz: Class<*>, p: Predicate<Field>): Collection<Field> {
            val result = mutableListOf<Field>()

            clazz.declaredFields.forEach {
                if (p.apply(it)) {
                    result.add(it)
                }
            }

            if (clazz.superclass != null) {
                result.addAll(eachField(clazz.superclass, p))
            }

            return result
        }

        fun eachMethod(clazz: Class<*>, p: Predicate<Method>): Collection<Method> {
            val result = mutableListOf<Method>()

            clazz.declaredMethods.forEach {
                if (p.apply(it)) {
                    result.add(it)
                }
            }

            if (clazz.superclass != null) {
                result.addAll(eachMethod(clazz.superclass, p))
            }

            return result
        }

        fun <T : Annotation> eachAnnotation(
                clazz: Class<*>,
                annotationClass: Class<T>, p: Predicate<T>,
        ) {

            var a = clazz.getAnnotation(annotationClass)

            if (a != null) {
                p.apply(a)
            }

            clazz.declaredMethods.forEach {
                a = it.getAnnotation(annotationClass)

                if (a != null) {
                    p.apply(a)
                }
            }

            if (clazz.superclass != null) {
                eachAnnotation(clazz.superclass, annotationClass, p)
            }
        }

        /**
         * Ignores case when comparing method names
         *
         * @param clazz
         * @param name
         * @return
         */
        fun findMethod(clazz: Class<*>, name: String): Method? {

            clazz.declaredMethods.forEach {
                if (it.name.equals(name, ignoreCase = true)) {
                    return it
                }
            }

            return if (clazz.superclass != null) {
                findMethod(clazz.superclass, name)
            } else null
        }

        fun hasAnnotation(e: AnnotatedElement, annotationClass: Class<out Annotation>): Boolean {
            return e.getAnnotation(annotationClass) != null
        }
    }
}

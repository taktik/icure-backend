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
package org.taktik.icure.domain.filter.impl.predicate

import com.github.pozo.KotlinBuilder
import org.apache.commons.beanutils.PropertyUtilsBean
import org.taktik.icure.domain.filter.predicate.Predicate
import org.taktik.icure.entities.base.Identifiable
import java.lang.reflect.InvocationTargetException

@KotlinBuilder
data class KeyValuePredicate(
        val key: String? = null,
        val operator: Operator? = null,
        val value: Any? = null
) : Predicate {
    val pub = PropertyUtilsBean()

    override fun apply(input: Identifiable<String>): Boolean {
        return try {
            operator!!.apply(pub.getProperty(input, key) as Comparable<Any>?, value as Comparable<Any>?)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        }
    }

    enum class Operator(val code: String, val lambda: (Comparable<Any>?, Comparable<Any>?) -> Boolean) {
        EQUAL("==", { a,b ->
            if (a != null && a is Number && b != null && b is Number) {
                if (a.toDouble() == b.toDouble()) true else a == b
            } else a == b
        }),
        NOTEQUAL("!=", { a,b -> !EQUAL.apply(a, b) }),
        GREATERTHAN(">", { a, b -> if(a == null && b == null) false else if(a == null) false else if (b==null) true else a > b }),
        SMALLERTHAN("<", { a, b -> if(a == null && b == null) false else if(a == null) false else if (b==null) true else a < b }),
        GREATERTHANOREQUAL(">=", { a, b -> if(a == null && b == null) false else if(a == null) false else if (b==null) true else a >= b }),
        SMALLERTHANOREQUAL("<=", { a, b -> if(a == null && b == null) false else if(a == null) false else if (b==null) true else a <= b }),
        LIKE("%=", { a, b -> b?.let { pattern -> a?.toString()?.matches(Regex(pattern.toString())) } ?: false }),
        ILIKE("%%=", { a, b -> b?.let { pattern -> a?.toString()?.toLowerCase()?.matches(Regex(pattern.toString().toLowerCase())) } ?: false });

        override fun toString(): String {
            return code
        }

        fun apply(a: Comparable<Any>?, b: Comparable<Any>?): Boolean {
            return lambda(a, b)
        }
    }
}

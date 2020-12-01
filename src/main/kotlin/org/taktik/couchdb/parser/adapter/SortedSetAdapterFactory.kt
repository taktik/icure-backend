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

package org.taktik.couchdb.parser.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

internal class SortedSetAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation?>, moshi: Moshi): JsonAdapter<*>? {
        if (!annotations.isEmpty()) {
            return null // Annotations? This factory doesn't apply.
        }
        if (type !is ParameterizedType) {
            return null // No type parameter? This factory doesn't apply.
        }
        val parameterizedType: ParameterizedType = type as ParameterizedType
        if (parameterizedType.getRawType() !== SortedSet::class.java) {
            return null // Not a sorted set? This factory doesn't apply.
        }
        val elementType: Type = parameterizedType.getActualTypeArguments().get(0)
        val elementAdapter = moshi.adapter<Any>(elementType)
        return SortedSetAdapter(elementAdapter).nullSafe()
    }
}

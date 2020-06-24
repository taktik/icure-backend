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

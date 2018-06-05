package org.taktik.couchdb.parser

import java.lang.reflect.ParameterizedType

class AsyncDeserializerImpl<T> : AsyncDeserializer {
    val wrapped : AsyncDeserializer

    private constructor(toWrap : AsyncDeserializer) {
        wrapped = toWrap
    }

    /**
     * POJOs + Arrays
     */
    constructor(baseClass: Class<T>,
                callback: (T) -> Unit) : this(_AsyncDeserializer(baseClass, callback))

    /**
     * Generic types (ex: {@code List<String>})
     */
    constructor(genericCollectionType: ParameterizedType,
                callback: (T) -> Unit) : this(_AsyncDeserializer(genericCollectionType, callback))

    override fun startObject() {
        try {
            wrapped.startObject()
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun endObject() {
        try {
            wrapped.endObject()
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun startArray() {
        try {
            wrapped.startArray()
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun endArray() {
        try {
            wrapped.endArray()
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun addField(fieldName: String): FluentValueStart {
        try {
            return wrapped.addField(fieldName)
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun stringValue(value: String) {
        try {
            wrapped.stringValue(value)
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun intValue(value: String) {
        try {
            wrapped.intValue(value)
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun doubleValue(value: String) {
        try {
            wrapped.doubleValue(value)
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun booleanValue(value: Boolean) {
        try {
            wrapped.booleanValue(value)
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun nullValue() {
        try {
            wrapped.nullValue()
        } catch (e: Exception) {
            throw EnrichedException(wrapped, e)
        }
    }

    override fun clean() {
        wrapped.clean()
    }

    override fun isClean(): Boolean {
        return wrapped.isClean()
    }

    override fun getCurrentBuildPathString(): String {
        return wrapped.getCurrentBuildPathString()
    }

    class EnrichedException(wrapped: AsyncDeserializer, e: Exception) : RuntimeException("error path: " + wrapped.getCurrentBuildPathString(), e)
}
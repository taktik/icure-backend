package org.taktik.couchdb.parser

import org.taktik.couchdb.parser._AsyncDeserializer.JsonValue.Partial
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.beans.PropertyDescriptor
import java.lang.reflect.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Internal implementation
 *
 * Caveat: does not handle inheritance
 */
internal class _AsyncDeserializer<T> : AsyncDeserializer {
    private val targetType: GenericType
    private val callback: (T) -> Unit
    private val objectsStack: LinkedList<Partial> = LinkedList()
    private var currentField: String? = null

    private constructor(targetType: GenericType,
                        callback: (T) -> Unit) {
        this.targetType = targetType
        this.callback = callback
    }

    /**
     * POJOs + Arrays
     */
    constructor(baseClass: Class<T>,
                callback: (T) -> Unit) : this(GenericType(baseClass), callback)

    /**
     * Generic types (ex: {@code List<String>})
     */
    constructor(genericCollectionType: ParameterizedType,
                callback: (T) -> Unit) : this(GenericType(genericCollectionType), callback)

    override fun startObject() {
        dispatchPartialJsonValue({ attributeName, inferredType ->
            Partial.ObjectValue(attributeName, inferredType)
        })
    }

    override fun endObject() {
        val currentObject = objectsStack.removeLast() ?: throw IllegalStateException("Unbalanced end object")
        if (currentObject !is Partial.ObjectValue) {
            throw IllegalStateException("endObject: object $currentObject found where array was expected.")
        }
        currentObject.value = instanciateObject(currentObject)
        if (objectsStack.isEmpty()) {
            @Suppress("UNCHECKED_CAST")
            callback(currentObject.value as T)
        }
    }

    override fun startArray() {
        dispatchPartialJsonValue({ attributeName, inferredType ->
            Partial.ArrayValue(attributeName, when (inferredType.type) {
                Any::class.java -> GenericType(Array<Any>::class.java) // support storing Json array to field of type Any
                else -> inferredType
            })
        })
    }

    override fun endArray() {
        val partial = objectsStack.removeLast() ?: throw IllegalStateException("Unbalanced end array")
        if (partial !is Partial.ArrayValue) {
            throw IllegalStateException("endArray: found wrong $partial.\n" +
                    "an object or an array was not correctly closed.")
        }
        partial.value = buildCollection(partial)
        if (objectsStack.isEmpty()) {
            @Suppress("UNCHECKED_CAST")
            callback(partial.value as T)
        }
    }

    override fun addField(fieldName: String): FluentValueStart {
        if (currentField != null) {
            throw IllegalStateException("need value between two calls to addField. "
                    + "Last property: ${this.currentField}}, "
                    + "current property: $fieldName")
        }
        checkFieldAvailablility(fieldName)
        currentField = fieldName
        return this
    }

    override fun stringValue(value: String) {
        dispatchPartialJsonValue { attributeName, inferredType ->
            JsonValue.StringValue(when (inferredType.rawClass) {
                Char::class.java, Char::class.javaObjectType, Char::class.javaPrimitiveType -> {
                    if (value.length != 1) {
                        throw IllegalArgumentException("only single-char strings are accepted as values for char fields: ${value}")
                    }
                    value[0]
                }
                else -> value
            })
        }
    }

    override fun intValue(value: String) {
        dispatchPartialJsonValue { attributeName, inferredType ->
            JsonValue.IntValue(when (inferredType.rawClass) {
                Byte::class.java, Byte::class.javaObjectType, Byte::class.javaPrimitiveType -> java.lang.Byte.parseByte(value)
                Short::class.java, Short::class.javaObjectType, Short::class.javaPrimitiveType -> java.lang.Short.parseShort(value)
                Int::class.java, Int::class.javaObjectType, Int::class.javaPrimitiveType -> Integer.parseInt(value)
                Long::class.java, Long::class.javaObjectType, Long::class.javaPrimitiveType -> java.lang.Long.parseLong(value)
                BigInteger::class.java -> BigInteger(value)
                BigDecimal::class.java, Any::class.java -> BigDecimal(value)
                else -> throw IllegalArgumentException("no matching integer type for ${inferredType}")
            })
        }
    }

    override fun doubleValue(value: String) {
        dispatchPartialJsonValue({ attributeName, inferredType ->
                JsonValue.DoubleValue(when (inferredType.rawClass) {
                    Float::class.java, Float::class.javaObjectType, Float::class.javaPrimitiveType -> {
                        val float = java.lang.Float.parseFloat(value)
                        if (!java.lang.Float.isFinite(float) && float.toString().toLowerCase() != value.toLowerCase()) {
                            throw NumberFormatException("value ${value} overflows to ${float}")
                        } else {
                            float
                        }
                    }
                    Double::class.java, Double::class.javaObjectType, Double::class.javaPrimitiveType -> {
                        val double = java.lang.Double.parseDouble(value)
                        if (!java.lang.Double.isFinite(double) && double.toString().toLowerCase() != value.toLowerCase()) {
                            throw NumberFormatException("value ${value} overflows to ${double}")
                        } else {
                            double
                        }
                    }
                    BigDecimal::class.java, Any::class.java -> BigDecimal(value)
                    else -> throw IllegalArgumentException("no matching decimal type for ${inferredType}")
                })
        })
    }

    override fun booleanValue(value: Boolean) {
        dispatchPartialJsonValue { attributeName, inferredType ->
            JsonValue.BooleanValue(value)
        }
    }

    override fun nullValue() {
        dispatchPartialJsonValue { attributeName, inferredType ->
            JsonValue.NullValue()
        }
    }

    /**
     * Will be called in two situations:
     *  1. When we consume the first { of the object
     *  2. When we have an object value for a field (after :)
     */
    private fun dispatchPartialJsonValue(valueFactory: (String?, GenericType) -> JsonValue) {
        val jsonValue: JsonValue
        if (objectsStack.isEmpty()) {
            jsonValue = valueFactory(null, targetType)
        } else {
            val parent = getCurrentPartial()
            when (parent) {
                is Partial.ArrayValue -> {
                    jsonValue = valueFactory(parent.elements.size.toString(), parent.inferredType.containedItemType)
                    parent.elements.add(jsonValue)
                }
                is Partial.ObjectValue -> {
                    val name = currentField!!
                    val inferredType = when {
                        parent.inferredType.rawClass == Any::class.java -> GenericType(Any::class.java)
                        parent.inferredType.isAssignableTo(Map::class.java) -> parent.inferredType.containedItemType
                        else -> GenericType(parent.inferredType.getDeclaredField(name)!!)
                    }
                    currentField = null
                    jsonValue = valueFactory(name, inferredType)
                    parent.attributes[name] = jsonValue
                }
            }
        }
        if (jsonValue is Partial) {
            objectsStack.add(jsonValue)
        }
    }

    private fun checkFieldAvailablility(fieldName: String) {
        val inferredType = getCurrentPartial().inferredType
        if (inferredType.type != Any::class.java && !inferredType.isMap && inferredType.type is Class<*>) {
            PropertyDescriptor(fieldName, inferredType.type).writeMethod
            inferredType.getDeclaredField(fieldName)
        }
    }

    private fun getCurrentPartial() = objectsStack[objectsStack.size - 1]

    override fun getCurrentBuildPathString(): String = getCurrentBuildPathString(objectsStack)

    sealed class JsonValue(var value: Any?) {

        class BooleanValue(value: Boolean) : JsonValue(value)

        class IntValue(value: Any) : JsonValue(value)

        class DoubleValue(value: Any) : JsonValue(value)

        class StringValue(value: Any) : JsonValue(value)

        class NullValue : JsonValue(null)

        sealed class Partial(val parentAttribute: String?, val inferredType: GenericType, value: Any?) : JsonValue(value) {

            class ObjectValue(
                    parentAttribute: String? = null,
                    inferredType: GenericType,
                    value: Any? = null,
                    val attributes: MutableMap<String, JsonValue> = mutableMapOf()
            ) : Partial(parentAttribute, inferredType, value)

            class ArrayValue(
                    parentAttribute: String? = null,
                    inferredType: GenericType,
                    value: Any? = null,
                    val elements: MutableList<JsonValue> = mutableListOf()
            ) : Partial(parentAttribute, inferredType, value)
        }
    }

    companion object {
        private val defaultCollectionType = ArrayList::class.java
        private val defaultMapType = LinkedHashMap::class.java

        private fun instanciateObject(element: Partial.ObjectValue): Any {
            val inferredType = element.inferredType
            if (inferredType.isAssignableTo(Map::class.java) || inferredType.rawClass == Any::class.java) {
                val mapType = getMapType(element)
                val data: Map<String, Any?> = element.attributes.mapValues { entry -> entry.value.value }
                if (MutableMap::class.java.isAssignableFrom(mapType)) {
                    @Suppress("UNCHECKED_CAST")
                    val map = mapType.newInstance() as MutableMap<String, Any?>
                    map.putAll(data)
                    return map
                } else {
                    return data
                }
            } else {
                val o = inferredType.rawClass.newInstance()
                for ((attribute, value) in element.attributes) {
                    assertPartialComplete(value)
                    PropertyDescriptor(attribute, inferredType.rawClass).writeMethod.invoke(o, value.value)
                }
                return o
            }
        }

        private fun assertPartialComplete(value: JsonValue) {
            if (value is Partial && value.value == null) {
                throw IllegalStateException("partial attributes should be complete before building parent object: $value")
            }
        }

        private fun buildCollection(element: Partial.ArrayValue): Any {
            if (element.inferredType.isCollection) {
                val collectionType: Class<*> = getCollectionType(element)
                // immutable collections might support constructor(Collection<T>)
                val collectionConstructor = collectionType.constructors.find { constructor ->
                    Modifier.isPublic(constructor.modifiers)
                            && constructor.parameterCount == 1
                            && constructor.parameterTypes[0].isAssignableFrom(List::class.java)
                            && Collection::class.java.isAssignableFrom(constructor.parameterTypes[0])
                }

                val values = element.elements.map { elem -> elem.value }
                return when (collectionConstructor) {
                    null -> {
                        @Suppress("UNCHECKED_CAST")
                        val collection = collectionType.newInstance() as MutableCollection<Any?>
                        collection.addAll(values)
                        collection
                    }
                    else -> collectionConstructor.newInstance(values) as Collection<*>
                }

            } else if (element.inferredType.isArray) {
                @Suppress("UNCHECKED_CAST")
                val array = java.lang.reflect.Array.newInstance(element.inferredType.arrayItemType.rawClass, element.elements.size)!! as Array<Any?>
                for (i in 0..element.elements.size - 1) {
                    array[i] = element.elements[i].value
                }
                return array
            } else if (element.inferredType.rawClass == Any::class.java) {
                val array = Array<Any?>(element.elements.size) {}
                for (i in 0..element.elements.size - 1) {
                    array[i] = element.elements[i].value
                }
                return array
            } else {
                throw IllegalStateException("unknow array/collection type: " + element.inferredType.rawClass)
            }
        }

        private fun getCollectionType(element: Partial.ArrayValue): Class<*> {
            val type = element.inferredType
            assert(type.isCollection)
            return when {
                type.isInterface && type.isAssignableFrom(defaultCollectionType) ->
                    defaultCollectionType
                type.isInterface ->
                    throw IllegalStateException("default collection is not compatible with interface ${type.type}")
                else -> type.rawClass
            }
        }

        private fun getMapType(element: Partial.ObjectValue): Class<*> {
            val type = element.inferredType
            assert(type.isAssignableTo(Map::class.java) || type.rawClass == Any::class.java)
            return when {
                type.rawClass == Any::class.java -> defaultMapType
                type.isInterface && type.isAssignableFrom(defaultMapType) ->
                    defaultMapType
                type.isInterface ->
                    throw IllegalStateException("default map is not compatible with interface ${type.type}")
                else -> type.rawClass
            }
        }

        private fun getCurrentBuildPathString(objectsBeingBuilt: LinkedList<Partial>): String {
            return objectsBeingBuilt.map { partial -> "${partial.parentAttribute ?: ""}(${partial.inferredType.rawClass.simpleName})" }.joinToString("/")
        }

        private fun stringToAnyMap() = ParameterizedTypeImpl.make(MutableMap::class.java, arrayOf(String::class.java, Any::class.java), null)!!
    }

    override fun clean() {
        objectsStack.clear()
        currentField = null
    }

    override fun isClean(): Boolean {
        return objectsStack.isEmpty()
    }
}

class GenericType(val type: Type) {

    val rawClass: Class<*> = when (type) {
        is Class<*> -> type
        is ParameterizedType -> type.rawType as? Class<*> ?: GenericType(type.rawType).rawClass
        is TypeVariable<*> -> Any::class.java
        is WildcardType -> if (type.upperBounds.size == 1) GenericType(type.upperBounds[0]).rawClass else Any::class.java
        is GenericArrayType -> java.lang.reflect.Array.newInstance(GenericType(type.genericComponentType).rawClass, 0).javaClass
        else -> type as Class<*> // ClassCastException
    }
    val isInterface: Boolean = rawClass.isInterface
    val isArray: Boolean = rawClass.isArray
    val isCollection: Boolean = isAssignableTo(Collection::class.java)
    val isMap: Boolean = isAssignableTo(Map::class.java)

    constructor(field: Field) : this(field.genericType)

    val arrayItemType: GenericType
        get() {
            val componentType = when (type) {
                is Class<*> -> type.componentType
                is GenericArrayType -> type.genericComponentType
                else -> throw IllegalStateException("type does not look like an array: $type")
            }
            return GenericType(componentType)
        }


    val collectionItemType: GenericType
        get() = GenericType((type as ParameterizedType).actualTypeArguments[0])

    val mapValueType: GenericType
        get() = GenericType((type as ParameterizedType).actualTypeArguments[1])

    val containedItemType: GenericType
        get() = when {
            isArray -> arrayItemType
            isCollection -> collectionItemType
            isMap -> mapValueType
            rawClass == Any::class.java -> GenericType(Any::class.java)
            else -> throw IllegalStateException("cannot get element type on something else than a collection or an array. Actual type of container: ${type}")
        }

    fun isAssignableFrom(clazz: Class<*>): Boolean {
        return rawClass.isAssignableFrom(clazz)
    }

    fun isAssignableTo(clazz: Class<*>): Boolean {
        return clazz.isAssignableFrom(rawClass)
    }

    fun getDeclaredField(fieldName: String): Field? {
        return rawClass.getDeclaredField(fieldName)
    }

    fun newInstance(): Any {
        return rawClass.newInstance()
    }

    override fun toString(): String {
        return "GenericType(type=$type, rawClass=$rawClass, isInterface=$isInterface, isArray=$isArray, isCollection=$isCollection, isMap=$isMap)"
    }
}

interface FluentValueStart {
    fun startObject()
    fun startArray()
    fun stringValue(value: String)
    fun intValue(value: String)
    fun doubleValue(value: String)
    fun booleanValue(value: Boolean)
    fun nullValue()
}


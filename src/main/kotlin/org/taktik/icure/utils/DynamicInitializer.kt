package org.taktik.icure.utils

import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

interface DynamicInitializer<T>

inline operator fun <reified C : Any, T : DynamicInitializer<C>> T.invoke(args: Map<String, Any?>): C {
    val constructor = C::class.primaryConstructor!!
    val argmap = HashMap<KParameter, Any?>().apply {
        constructor.parameters.forEach { if (it.name in args) put(it, args[it.name]) }
    }
    return constructor.callBy(argmap)
}

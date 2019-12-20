/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.rest.handlers

import com.google.common.base.Preconditions
import com.google.gson.*
import org.reflections.Reflections
import org.reflections.scanners.TypeAnnotationsScanner
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.util.*

class DiscriminatedTypeAdapter<T:Any>(clazz: Class<T>) : JsonSerializer<T>, JsonDeserializer<T> {
    private val discriminator = clazz.getAnnotation(JsonDiscriminator::class.java)?.value ?: "\$type"
    private val subclasses: MutableMap<String, Class<*>> = HashMap()
    private val reverseSubclasses: MutableMap<Class<*>, String> = HashMap()
    private val scanner = Reflections(clazz, TypeAnnotationsScanner())

    override fun serialize(srcObject: T, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val el = context.serialize(srcObject, srcObject.javaClass)
        val result = el.asJsonObject
        val discr = reverseSubclasses[srcObject.javaClass]
                ?: throw JsonParseException("Invalid subclass " + srcObject.javaClass)
        result.addProperty(discriminator, discr)
        return result
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T {
        val `object` = json.asJsonObject
        val discr = `object`[discriminator]
                ?: throw JsonParseException("Missing discriminator $discriminator in object")
        val selectedSubClass = subclasses[discr.asString]
                ?: throw JsonParseException("Invalid subclass " + discr.asString + " in object")
        return context.deserialize(`object`, selectedSubClass)
    }

    init {
        Preconditions.checkArgument(clazz.isInterface || Modifier.isAbstract(clazz.modifiers), "Superclass must be abstract")
        val classes = scanner.getTypesAnnotatedWith(JsonPolymorphismRoot::class.java).filter { clazz.isAssignableFrom(it) }
        for (subClass in classes) {
            val discriminated = subClass.getAnnotation(JsonDiscriminated::class.java)
            val discriminatedString = discriminated?.value ?: subClass.simpleName
            subclasses[discriminatedString] = subClass
            reverseSubclasses[subClass] = discriminatedString
        }

    }
}

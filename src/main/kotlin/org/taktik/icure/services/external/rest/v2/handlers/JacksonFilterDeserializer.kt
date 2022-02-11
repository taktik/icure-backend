/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v2.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.google.common.base.Preconditions
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.springframework.boot.jackson.JsonObjectDeserializer
import org.taktik.icure.handlers.JsonDiscriminated
import org.taktik.icure.handlers.JsonDiscriminator
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import java.lang.reflect.Modifier
import java.util.*


class JacksonFilterDeserializer : JsonObjectDeserializer<AbstractFilterDto<*>>() {
    private val discriminator = AbstractFilterDto::class.java.getAnnotation(JsonDiscriminator::class.java)?.value ?: "\$type"
    private val subclasses: MutableMap<String, Class<AbstractFilterDto<*>>> = HashMap()
    private val reverseSubclasses: MutableMap<Class<*>, String> = HashMap()
    private val scanner = Reflections(AbstractFilterDto::class.java, TypeAnnotationsScanner(), SubTypesScanner())


    init {
        Preconditions.checkArgument(Modifier.isAbstract(AbstractFilterDto::class.java.modifiers), "Superclass must be abstract")
        val classes = scanner.getTypesAnnotatedWith(JsonPolymorphismRoot::class.java).filter { AbstractFilterDto::class.java.isAssignableFrom(it) }
        for (subClass in classes) {
            val discriminated = subClass.getAnnotation(JsonDiscriminated::class.java)
            val discriminatedString = discriminated?.value ?: subClass.simpleName
            subclasses[discriminatedString] = subClass as Class<AbstractFilterDto<*>>
            reverseSubclasses[subClass] = discriminatedString
        }

    }

    override fun deserializeObject(jsonParser: JsonParser?, context: DeserializationContext?, codec: ObjectCodec, tree: JsonNode): AbstractFilterDto<*> {
        val discr = tree[discriminator].textValue() ?: throw IllegalArgumentException("Missing discriminator $discriminator in object")
        val selectedSubClass = subclasses[discr] ?: throw IllegalArgumentException("Invalid subclass $discr in object")
        return codec.treeToValue(tree, selectedSubClass)
    }
}

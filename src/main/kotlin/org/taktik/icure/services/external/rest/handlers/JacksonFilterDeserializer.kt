package org.taktik.icure.services.external.rest.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.google.common.base.Preconditions
import com.google.gson.Gson
import com.google.gson.JsonParseException
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.springframework.boot.jackson.JsonObjectDeserializer
import org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto
import java.lang.reflect.Modifier
import java.util.*


class JacksonFilterDeserializer<T : FilterDto<*>> : JsonObjectDeserializer<T>() {
    private val discriminator = FilterDto::class.java.getAnnotation(JsonDiscriminator::class.java)?.value ?: "\$type"
    private val subclasses: MutableMap<String, Class<*>> = HashMap()
    private val reverseSubclasses: MutableMap<Class<*>, String> = HashMap()
    private val scanner = Reflections(FilterDto::class.java, TypeAnnotationsScanner(), SubTypesScanner())


    init {
        Preconditions.checkArgument(Modifier.isAbstract(FilterDto::class.java.modifiers), "Superclass must be abstract")
        val classes = scanner.getTypesAnnotatedWith(JsonPolymorphismRoot::class.java).filter { FilterDto::class.java.isAssignableFrom(it) }
        for (subClass in classes) {
            val discriminated = subClass.getAnnotation(JsonDiscriminated::class.java)
            val discriminatedString = discriminated?.value ?: subClass.simpleName
            subclasses[discriminatedString] = subClass
            reverseSubclasses[subClass] = discriminatedString
        }

    }

    override fun deserializeObject(jsonParser: JsonParser?, context: DeserializationContext?, codec: ObjectCodec, tree: JsonNode): T {
        val discr = tree[discriminator].textValue()
                ?: throw JsonParseException("Missing discriminator $discriminator in object")
        val selectedSubClass = subclasses[discr]
                ?: throw JsonParseException("Invalid subclass " + discr + " in object")
        val gson = Gson()
        return gson.fromJson<T>(tree.toString(), selectedSubClass)
    }
}

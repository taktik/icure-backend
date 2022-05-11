/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v2.handlers

import java.lang.reflect.Modifier
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
import org.taktik.icure.services.external.rest.v2.dto.gui.Editor

class JacksonEditorDeserializer : JsonObjectDeserializer<Editor>() {
	private val discriminator = Editor::class.java.getAnnotation(JsonDiscriminator::class.java)?.value ?: "key"
	private val subclasses: MutableMap<String, Class<Editor>> = HashMap()
	private val reverseSubclasses: MutableMap<Class<*>, String> = HashMap()
	private val scanner = Reflections(Editor::class.java, TypeAnnotationsScanner(), SubTypesScanner())

	init {
		Preconditions.checkArgument(Modifier.isAbstract(Editor::class.java.modifiers), "Superclass must be abstract")
		val classes = scanner.getTypesAnnotatedWith(JsonPolymorphismRoot::class.java).filter { Editor::class.java.isAssignableFrom(it) }
		for (subClass in classes) {
			val discriminated = subClass.getAnnotation(JsonDiscriminated::class.java)
			val discriminatedString = discriminated?.value ?: subClass.simpleName
			subclasses[discriminatedString] = subClass as Class<Editor>
			reverseSubclasses[subClass] = discriminatedString
		}
	}

	override fun deserializeObject(jsonParser: JsonParser?, context: DeserializationContext?, codec: ObjectCodec, tree: JsonNode): Editor {
		val discr = tree[discriminator].textValue() ?: throw IllegalArgumentException("Missing discriminator $discriminator in object")
		val selectedSubClass = subclasses[discr] ?: throw IllegalArgumentException("Invalid subclass $discr in object")
		return codec.treeToValue(tree, selectedSubClass)
	}
}

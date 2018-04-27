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

package org.taktik.icure.services.external.rest.handlers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

public class DiscriminatedTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private String discriminator;
    private Map<String, Class> subclasses = new HashMap<>();
    private Map<Class, String> reverseSubclasses = new HashMap<>();

    public DiscriminatedTypeAdapter(Class<T> clazz) {
        Preconditions.checkArgument(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()),"Superclass must be abstract");
        JsonPolymorphismSupport annotation = clazz.getAnnotation(JsonPolymorphismSupport.class);
        Preconditions.checkNotNull(annotation, "Superclass must be annotated with JsonPolymorphismSupport");

        @SuppressWarnings("unchecked")
        Class<T>[] classes = (Class<T>[]) annotation.value();
        for (Class<T> subClass : classes) {
            JsonDiscriminated discriminated = subClass.getAnnotation(JsonDiscriminated.class);
            String discriminatedString = discriminated != null ? discriminated.value() : subClass.getSimpleName();
            subclasses.put(discriminatedString, subClass);
            reverseSubclasses.put(subClass, discriminatedString);
        }
        JsonDiscriminator discriminatorAnnotation = clazz.getAnnotation(JsonDiscriminator.class);
        discriminator = discriminatorAnnotation != null ? discriminatorAnnotation.value() : "$type";
    }

    public JsonElement serialize(T object, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement el = context.serialize(object, object.getClass());

        JsonObject result = el.getAsJsonObject();

        String discr = reverseSubclasses.get(object.getClass());
        if (discr == null) {
            throw new JsonParseException("Invalid subclass " + object.getClass());
        }

        result.addProperty(discriminator, discr);

        return result;
    }

    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        JsonElement discr = object.get(discriminator);

        if (discr == null) {
            throw new JsonParseException("Missing discriminator " + discriminator + " in object");
        }

        Class selectedSubClass = subclasses.get(discr.getAsString());
        if (selectedSubClass == null) {
            throw new JsonParseException("Invalid subclass " + discr.getAsString() + " in object");
        }
        return context.deserialize(object, selectedSubClass);
    }
}

/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
	@Override
	public ObjectMapper getContext(Class<?> type) {
		// TypeResolver
		SimpleAbstractTypeResolver abstractTypeResolver = new SimpleAbstractTypeResolver();
		abstractTypeResolver.addMapping(List.class, ArrayList.class);
		abstractTypeResolver.addMapping(Set.class, HashSet.class);
		abstractTypeResolver.addMapping(Map.class, HashMap.class);

		// Serializer
		DefaultSerializerProvider serializerProvider = new DefaultSerializerProvider.Impl();

		// Deserializer
		DeserializerFactoryConfig deserializerFactoryConfig = new DeserializerFactoryConfig().withAbstractTypeResolver(abstractTypeResolver);
		BeanDeserializerFactory deserializerFactory = new BeanDeserializerFactory(deserializerFactoryConfig);
		DefaultDeserializationContext.Impl deserializationContext = new DefaultDeserializationContext.Impl(deserializerFactory);

		// ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper(null, serializerProvider, deserializationContext);
		objectMapper = objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "type");
		objectMapper = objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper = objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		return objectMapper;
	}
}
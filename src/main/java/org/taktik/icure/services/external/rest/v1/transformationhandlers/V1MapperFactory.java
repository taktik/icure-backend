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

package org.taktik.icure.services.external.rest.v1.transformationhandlers;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.taktik.icure.db.PaginatedDocumentKeyIdPair;
import org.taktik.icure.dto.filter.chain.FilterChain;
import org.taktik.icure.dto.filter.predicate.AndPredicate;
import org.taktik.icure.dto.filter.predicate.KeyValuePredicate;
import org.taktik.icure.dto.filter.predicate.NotPredicate;
import org.taktik.icure.dto.filter.predicate.OrPredicate;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeFlag;
import org.taktik.icure.logic.impl.filter.patient.*;
import org.taktik.icure.services.external.rest.v1.dto.CodeDto;
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout;
import org.taktik.icure.logic.impl.filter.Filters;
import org.taktik.icure.logic.impl.filter.contact.ContactByHcPartyTagCodeDateFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyAndExternalIdFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyAndSsinFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyAndSsinsFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyDateOfBirthFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyNameContainsFuzzyFilter;
import org.taktik.icure.logic.impl.filter.service.ServiceByContactsAndSubcontactsFilter;
import org.taktik.icure.logic.impl.filter.service.ServiceByHcPartyTagCodeDateFilter;

import java.time.Instant;
import java.util.stream.Collectors;

public class V1MapperFactory {
	private Gson gsonMapper;

	public V1MapperFactory() {
	}

	public V1MapperFactory(Gson gsonMapper) {
		this.gsonMapper = gsonMapper;
	}

	public void setGsonMapper(Gson gsonMapper) {
		this.gsonMapper = gsonMapper;
	}

	public MapperFacade getMapper() {
        MapperFactory factory = new DefaultMapperFactory.Builder().build();

		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain.class, FilterChain.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.predicate.KeyValuePredicate.class, KeyValuePredicate.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.predicate.OrPredicate.class, OrPredicate.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.predicate.AndPredicate.class, AndPredicate.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.predicate.NotPredicate.class, NotPredicate.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByHcPartyTagCodeDateFilter.class, ContactByHcPartyTagCodeDateFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByContactsAndSubcontactsFilter.class, ServiceByContactsAndSubcontactsFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByHcPartyTagCodeDateFilter.class, ServiceByHcPartyTagCodeDateFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndExternalIdFilter.class, PatientByHcPartyAndExternalIdFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndSsinFilter.class, PatientByHcPartyAndSsinFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndSsinsFilter.class, PatientByHcPartyAndSsinsFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyDateOfBirthFilter.class, PatientByHcPartyDateOfBirthFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter.class, PatientByHcPartyNameContainsFuzzyFilter.class).byDefault().toClassMap());
        factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyNameFilter.class, PatientByHcPartyNameFilter.class).byDefault().toClassMap());
        factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.Filters.ComplementFilter.class, Filters.ComplementFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.Filters.IntersectionFilter.class, Filters.IntersectionFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.Filters.UnionFilter.class, Filters.UnionFilter.class).byDefault().toClassMap());

		ConverterFactory converterFactory = factory.getConverterFactory();
		converterFactory.registerConverter(new CustomConverter<LocalDate, Long>() {
			@Override
			public Long convert(LocalDate source, Type<? extends Long> destinationType, MappingContext mappingContext) {
				return source.toDate().getTime();
			}
		});

		converterFactory.registerConverter(new CustomConverter<Instant, Long>() {
			@Override
			public Long convert(Instant source, Type<? extends Long> destinationType, MappingContext mappingContext) {
				return source.toEpochMilli();
			}
		});

		converterFactory.registerConverter(new CustomConverter<Long, Instant>() {
			@Override
			public Instant convert(Long source, Type<? extends Instant> destinationType, MappingContext mappingContext) {
				return Instant.ofEpochMilli(source);
			}
		});

        converterFactory.registerConverter(new CustomConverter<Instant, Instant>() {
			@Override
			public Instant convert(Instant source, Type<? extends Instant> destinationType, MappingContext mappingContext) {
				return Instant.ofEpochSecond(source.getEpochSecond(), source.getNano());
			}
		});

		converterFactory.registerConverter(new CustomConverter<DateTime,Instant>() {
			@Override
			public Instant convert(DateTime source, Type<? extends Instant> destinationType, MappingContext mappingContext) {
				return Instant.ofEpochMilli(source.getMillis());
			}
		});

		converterFactory.registerConverter(new CustomConverter<DateTime,Long>() {
			@Override
			public Long convert(DateTime source, Type<? extends Long> destinationType, MappingContext mappingContext) {
				return source.getMillis();
			}
		});

		converterFactory.registerConverter(new CustomConverter<PaginatedDocumentKeyIdPair,org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair>() {
			@Override
			public org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair convert(PaginatedDocumentKeyIdPair source, Type<? extends org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair> destinationType, MappingContext mappingContext) {
				return new org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair<>(source.getStartKey(),source.getStartKeyDocId());
			}
		});

		converterFactory.registerConverter(new CustomConverter<FormLayout,byte[]>() {
			@Override
			public byte[] convert(FormLayout source, Type<? extends byte[]> destinationType, MappingContext mappingContext) {
				return gsonMapper.toJson(source).getBytes(Charsets.UTF_8);
			}
		});

		converterFactory.registerConverter(new CustomConverter<byte[], FormLayout>() {
			@Override
			public FormLayout convert(byte[] source, Type<? extends FormLayout> destinationType, MappingContext mappingContext) {
				try {
					return gsonMapper.fromJson(new String(source, Charsets.UTF_8), FormLayout.class);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});

		converterFactory.registerConverter(new CustomConverter<CodeDto,Code>() {
			@Override
			public Code convert(CodeDto source, Type<? extends Code> destinationType, MappingContext mappingContext) {
				String defVersion =  Code.versionsMap.get(source.getType());
				Code c = new Code(source.getType(), source.getCode(), source.getVersion() != null ? source.getVersion() : defVersion != null ? defVersion : "1");

				if (source.getId() != null) { c.setId(source.getId()); }
				if (source.getFlags() != null) { c.setFlags(source.getFlags().stream().map(i->mapperFacade.map(i, CodeFlag.class)).collect(Collectors.toSet())); }

				c.setLinks(source.getLinks());
				c.setLabel(source.getLabel());
				c.setLevel(source.getLevel());
				c.setRegions(source.getRegions());
				c.setSearchTerms(source.getSearchTerms());
				c.setDeletionDate(source.getDeletionDate());
				c.setRev(source.getRev());

				return c;
			}
		});


		return factory.getMapperFacade();
    }
}

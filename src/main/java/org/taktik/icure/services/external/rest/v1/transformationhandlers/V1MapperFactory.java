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

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
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
import org.taktik.icure.be.ehealth.dto.dmg.DmgAcknowledge;
import org.taktik.icure.be.ehealth.dto.dmg.DmgClosure;
import org.taktik.icure.be.ehealth.dto.dmg.DmgConsultation;
import org.taktik.icure.be.ehealth.dto.dmg.DmgExtension;
import org.taktik.icure.be.ehealth.dto.dmg.DmgInscription;
import org.taktik.icure.be.ehealth.dto.dmg.DmgNotification;
import org.taktik.icure.be.ehealth.dto.dmg.DmgRegistration;
import org.taktik.icure.be.ehealth.dto.dmg.DmgsList;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType10;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType20;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType30;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType50;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType51;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType52;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType80;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType90;
import org.taktik.icure.db.PaginatedDocumentKeyIdPair;
import org.taktik.icure.dto.filter.chain.FilterChain;
import org.taktik.icure.dto.filter.predicate.AndPredicate;
import org.taktik.icure.dto.filter.predicate.KeyValuePredicate;
import org.taktik.icure.dto.filter.predicate.NotPredicate;
import org.taktik.icure.dto.filter.predicate.OrPredicate;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeFlag;
import org.taktik.icure.services.external.rest.v1.dto.CodeDto;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.KmehrCd;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.KmehrId;
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout;
import org.taktik.icure.logic.impl.filter.Filters;
import org.taktik.icure.logic.impl.filter.contact.ContactByHcPartyTagCodeDateFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyAndExternalIdFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyAndSsinFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyDateOfBirthFilter;
import org.taktik.icure.logic.impl.filter.patient.PatientByHcPartyNameContainsFuzzyFilter;
import org.taktik.icure.logic.impl.filter.service.ServiceByContactsAndSubcontactsFilter;
import org.taktik.icure.logic.impl.filter.service.ServiceByHcPartyTagCodeDateFilter;
import org.w3._2005._05.xmlmime.Base64Binary;

import java.time.Instant;
import java.util.Base64;
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
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyDateOfBirthFilter.class, PatientByHcPartyDateOfBirthFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter.class, PatientByHcPartyNameContainsFuzzyFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.Filters.ComplementFilter.class, Filters.ComplementFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.Filters.IntersectionFilter.class, Filters.IntersectionFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.filter.Filters.UnionFilter.class, Filters.UnionFilter.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgAcknowledge.class, DmgAcknowledge.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgClosure.class, DmgClosure.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgConsultation.class, DmgConsultation.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgExtension.class, DmgExtension.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgInscription.class, DmgInscription.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgNotification.class, DmgNotification.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgRegistration.class, DmgRegistration.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgsList.class, DmgsList.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType10.class, InvoiceRecordType10.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType20.class, InvoiceRecordType20.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType30.class, InvoiceRecordType30.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType50.class, InvoiceRecordType50.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType51.class, InvoiceRecordType51.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType52.class, InvoiceRecordType52.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType80.class, InvoiceRecordType80.class).byDefault().toClassMap());
		factory.registerClassMap(factory.classMap(org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments.InvoiceRecordType90.class, InvoiceRecordType90.class).byDefault().toClassMap());

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
		converterFactory.registerConverter(new CustomConverter<org.w3._2005._05.xmlmime.Base64Binary, String>() {
			@Override
			public String convert(Base64Binary base64Binary, Type<? extends String> destinationType, MappingContext mappingContext)  {
				return Base64.getEncoder().encodeToString(base64Binary.getValue());
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

		converterFactory.registerConverter(new CustomConverter<IDPATIENT,KmehrId>() {
			@Override
			public KmehrId convert(IDPATIENT source, Type<? extends KmehrId> destinationType, MappingContext mappingContext) {
				return new KmehrId(source.getS().value(),source.getSL(),source.getSV(),source.getValue());
			}
		});

		converterFactory.registerConverter(new CustomConverter<CDCONSENT,KmehrCd>() {
			@Override
			public KmehrCd convert(CDCONSENT source, Type<? extends KmehrCd> destinationType, MappingContext mappingContext) {
				return new KmehrCd(source.getS().value(),source.getSL(),source.getSV(),source.getValue().value());
			}
		});

		converterFactory.registerConverter(new CustomConverter<IDHCPARTY,KmehrId>() {
			@Override
			public KmehrId convert(IDHCPARTY source, Type<? extends KmehrId> destinationType, MappingContext mappingContext) {
				return new KmehrId(source.getS().value(),source.getSL(),source.getSV(),source.getValue());
			}
		});

		converterFactory.registerConverter(new CustomConverter<IDKMEHR,KmehrId>() {
			@Override
			public KmehrId convert(IDKMEHR source, Type<? extends KmehrId> destinationType, MappingContext mappingContext) {
				return new KmehrId(source.getS().value(),source.getSL(),source.getSV(),source.getValue());
			}
		});

		converterFactory.registerConverter(new CustomConverter<CDHCPARTY,KmehrCd>() {
			@Override
			public KmehrCd convert(CDHCPARTY source, Type<? extends KmehrCd> destinationType, MappingContext mappingContext) {
				return new KmehrCd(source.getS().value(),source.getSL(),source.getSV(),source.getValue());
			}
		});

		converterFactory.registerConverter(new CustomConverter<KmehrId, IDPATIENT>() {
			@Override
			public IDPATIENT convert(KmehrId source, Type<? extends IDPATIENT> destinationType, MappingContext mappingContext) {
				IDPATIENT idpatient = new IDPATIENT();

				idpatient.setS(IDPATIENTschemes.fromValue(source.getS()));
				idpatient.setSL(source.getSl());
				idpatient.setSV(source.getSv());
				idpatient.setValue(source.getValue());

				return idpatient;
			}
		});

		converterFactory.registerConverter(new CustomConverter<KmehrCd, CDCONSENT>() {
			@Override
			public CDCONSENT convert(KmehrCd source, Type<? extends CDCONSENT> destinationType, MappingContext mappingContext) {
				CDCONSENT cdconsent = new CDCONSENT();

				cdconsent.setS(CDCONSENTschemes.fromValue(source.getS()));
				cdconsent.setSL(source.getSl());
				cdconsent.setSV(source.getSv());
				cdconsent.setValue(CDCONSENTvalues.fromValue(source.getValue()));

				return cdconsent;
			}
		});

		converterFactory.registerConverter(new CustomConverter<KmehrId, IDHCPARTY>() {
			@Override
			public IDHCPARTY convert(KmehrId source, Type<? extends IDHCPARTY> destinationType, MappingContext mappingContext) {
				IDHCPARTY idpatient = new IDHCPARTY();

				idpatient.setS(IDHCPARTYschemes.fromValue(source.getS()));
				idpatient.setSL(source.getSl());
				idpatient.setSV(source.getSv());
				idpatient.setValue(source.getValue());

				return idpatient;
			}
		});

		converterFactory.registerConverter(new CustomConverter<KmehrCd, CDHCPARTY>() {
			@Override
			public CDHCPARTY convert(KmehrCd source, Type<? extends CDHCPARTY> destinationType, MappingContext mappingContext) {
				CDHCPARTY cdconsent = new CDHCPARTY();

				cdconsent.setS(CDHCPARTYschemes.fromValue(source.getS()));
				cdconsent.setSL(source.getSl());
				cdconsent.setSV(source.getSv());
				cdconsent.setValue(source.getValue());

				return cdconsent;
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

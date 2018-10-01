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

package org.taktik.icure.be.mikrono.impl;

import ma.glasnost.orika.MapperFacade;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.taktik.icure.be.mikrono.MikronoPatientLogic;
import org.taktik.icure.be.mikrono.dto.ChangeExternalIDReplyDto;
import org.taktik.icure.be.mikrono.dto.ChangeExternalIDRequestDto;
import org.taktik.icure.be.mikrono.dto.ListPatientsDto;
import org.taktik.icure.be.mikrono.dto.PatientDTO;
import org.taktik.icure.be.mikrono.dto.kmehr.Address;
import org.taktik.icure.be.mikrono.dto.kmehr.Person;
import org.taktik.icure.be.mikrono.dto.kmehr.Telecom;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.AddressType;
import org.taktik.icure.entities.embed.Gender;
import org.taktik.icure.entities.embed.TelecomType;
import org.taktik.icure.utils.FuzzyValues;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.core.Context;

/**
 * Created by aduchate on 16/12/11, 11:46
 */
public class MikronoPatientLogicImpl implements MikronoPatientLogic {
	private MapperFacade mapper;
	private String applicationToken;

	private RestTemplate restTemplate = new RestTemplate();
	{
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	}

	public MikronoPatientLogicImpl(String applicationToken) {
		this.applicationToken = applicationToken;
	}

	private Person getPersonFromPatient(Patient p) {
		Person patient = new Person();

		patient.setFirstname(p.getFirstName());
		patient.setFamilyname(p.getLastName());
		try {
			patient.setBirthdate(p.getDateOfBirth() != null ? Date.from(FuzzyValues.getDateTime(p.getDateOfBirth()).atZone(ZoneId.systemDefault()).toInstant()) : null);
		} catch (NullPointerException|DateTimeException ignored) {}
		if (p.getGender()!= null) { patient.setSex(p.getGender().getCode()); }
		if (p.getSsin()!=null) {patient.addId("ID-PATIENT:"+p.getSsin()); }
		p.getAddresses().forEach(a -> {
			Address address = mapper.map(a, Address.class);

			address.setZip(a.getPostalCode());
			String addressType = a.getAddressType() == null ? "home" : a.getAddressType().name();
			address.getTypes().add("CD-ADDRESS:"+ addressType);

			a.getTelecoms().forEach(t -> patient.getTelecoms().add(new Telecom(t.getTelecomNumber(),addressType,t.getTelecomType()==null?"email":t.getTelecomType().name())));

			patient.addAddress(address);
		});
		return patient;
	}

	private Patient getPatientFromDto(PatientDTO dto) {
		Person p = dto.getPatient();

		Patient patient = new Patient();
		patient.setId(dto.getExternalId());
		patient.setFirstName(p.getFirstname());
		patient.setLastName(p.getFamilyname());

		try {
			patient.setDateOfBirth(p.getBirthdate() != null ? (int) FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(p.getBirthdate().toInstant(), ZoneId.systemDefault()), ChronoUnit.DAYS) : null);
		} catch (NullPointerException ignored) {}

		if (p.getSex() != null) { patient.setGender(Gender.fromCode(p.getSex())); }
		if (p.getId("ID-PATIENT") != null) { patient.setSsin(p.getId("ID-PATIENT")); }

		p.getAddresses().forEach(a -> {
			org.taktik.icure.entities.embed.Address address = mapper.map(a, org.taktik.icure.entities.embed.Address.class);
			if (address.getAddressType() == null) { address.setAddressType(AddressType.home); }
			address.setPostalCode(a.getZip());
			if (a.getTypes() != null && a.getTypes().size()>0) {
				address.setAddressType(AddressType.valueOf(a.getTypes().stream().filter(typ->typ.startsWith("CD-ADDRESS:")).findAny().orElse("CD-ADDRESS:home").replaceAll("CD-ADDRESS:","")));
			}
			patient.getAddresses().add(address);
		});

		p.getTelecoms().forEach(t -> patient.getAddresses().forEach(a -> {
			if (a.getAddressType() != null && a.getAddressType().name().equals(t.getLocation())) {
				a.getTelecoms().add(new org.taktik.icure.entities.embed.Telecom(TelecomType.valueOf(t.getType()), t.getAddress()));
			}
		}));

		return patient;
	}

	private HttpHeaders getHttpHeaders(String mikronoUser, String mikronoPassword) {
		String plainCreds = mikronoPassword.matches("[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}")?(mikronoUser+":"+applicationToken+";"+mikronoPassword):(mikronoUser+":"+mikronoPassword);
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	@Override
    public void updatePatients(String url, Collection<Patient> patients, String mikronoUser, String mikronoPassword) {
		for (Patient p : patients) {
			PatientDTO dto = new PatientDTO();
			dto.setPatient(getPersonFromPatient(p));
			dto.setExternalId(p.getId());

			restTemplate.exchange(StringUtils.chomp(url,"/")+ "/rest/kmehrPatientByExternalId/{externalId}", HttpMethod.PUT, new HttpEntity<>(dto, getHttpHeaders(mikronoUser, mikronoPassword)), String.class, p.getId());
		}
    }

	@Override
    public List<Long> createPatients(String url, Collection<Patient> patients, String mikronoUser, String mikronoPassword) {
		List<Long> result = new ArrayList<>();
		for (Patient p : patients) {
			PatientDTO dto = new PatientDTO();
			dto.setPatient(getPersonFromPatient(p));
			dto.setExternalId(p.getId());

			result.add(restTemplate.exchange(StringUtils.chomp(url, "/") + "/rest/kmehrPatients", HttpMethod.POST, new HttpEntity<>(dto, getHttpHeaders(mikronoUser, mikronoPassword)), Long.class).getBody());
		}

		return result;
    }

    @Override
    public Patient loadPatient(String url, String id, String mikronoUser, String mikronoPassword) {
		return getPatientFromDto(restTemplate.exchange(StringUtils.chomp(url, "/") + "/rest/kmehrPatient/{id}", HttpMethod.GET, new HttpEntity<>(getHttpHeaders(mikronoUser, mikronoPassword)), PatientDTO.class, id).getBody());
    }

	@Override
	public ChangeExternalIDReplyDto updateExternalIds(String url, Map<String, String> ids, String mikronoUser, String mikronoPassword) {
		ResponseEntity<ChangeExternalIDReplyDto> exchange = restTemplate.exchange(StringUtils.chomp(url, "/") + "/rest/changeExternalIds", HttpMethod.PUT, new HttpEntity<>(new ChangeExternalIDRequestDto(ids), getHttpHeaders(mikronoUser, mikronoPassword)), ChangeExternalIDReplyDto.class);

		if (exchange.getStatusCode() != HttpStatus.OK) {
			throw new IllegalStateException();
		}

		return exchange.getBody();
	}

	@Override
    public void updatePatientId(String url, String id, String externalId, String mikronoUser, String mikronoPassword) {
		restTemplate.exchange(StringUtils.chomp(url,"/")+ "/rest/kmehrPatient/{id}", HttpMethod.PUT, new HttpEntity<>(externalId, getHttpHeaders(mikronoUser, mikronoPassword)), String.class, id);
    }

    @Override
    public Patient loadPatientWithIcureId(String url, String id, String mikronoUser, String mikronoPassword) {
		return getPatientFromDto(restTemplate.exchange(StringUtils.chomp(url, "/") + "/rest/kmehrPatientByExternalId/{id}", HttpMethod.GET, new HttpEntity<>(getHttpHeaders(mikronoUser, mikronoPassword)), PatientDTO.class, id).getBody());
    }

    @Override
    public List<String> listPatients(String url, Date fromDate, String mikronoUser, String mikronoPassword) {
		if (fromDate==null) { fromDate = new Date(0L); }
		return restTemplate.exchange(StringUtils.chomp(url, "/") + "/rest/patients/{from}", HttpMethod.GET, new HttpEntity<>(getHttpHeaders(mikronoUser, mikronoPassword)), ListPatientsDto.class, fromDate.getTime()).getBody().getPatients();
    }

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

}

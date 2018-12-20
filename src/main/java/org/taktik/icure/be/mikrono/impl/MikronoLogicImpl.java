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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.taktik.icure.be.mikrono.MikronoLogic;
import org.taktik.icure.dto.message.EmailOrSmsMessage;
import org.taktik.icure.services.external.rest.v1.dto.AppointmentDto;
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentsDto;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MikronoLogicImpl implements MikronoLogic {
	private Map<String,String> tokensPerServer;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final RestTemplate restTemplate = new RestTemplate();
	private String applicationToken;

	public MikronoLogicImpl(String applicationToken, String defaultServer, String defaultSuperUser, String defaultSuperToken) {
		this.applicationToken = applicationToken;
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		this.tokensPerServer = new HashMap<>();
		if (defaultServer != null && defaultSuperUser != null && defaultSuperToken != null) {
			this.tokensPerServer.put(defaultServer, String.join(":", defaultSuperUser, defaultSuperToken));
		}
		log.info("Mikrono Logic initialised");
	}

	private HttpHeaders getSuperUserHttpHeaders(String server) {
		String plainCreds = tokensPerServer.get(server);
		if (plainCreds == null) { return null; }

		//Insert application token
		String[] parts = plainCreds.split(":");
		String userToken = parts[1];
		plainCreds = parts[0] + ":" + getPassword(userToken);

		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private HttpHeaders getUserHttpHeaders(String server, String email, String userPassword) {
		byte[] plainCredsBytes = (email+":"+userPassword).getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}


	@Override
	@NotNull
	public String getPassword(String userToken) {
		return applicationToken + ";" + userToken;
	}

	class RegisterInfo implements Serializable {
		String id;
		String token;

		public RegisterInfo() {
		}

		RegisterInfo(String id, String token) {
			this.id = id;
			this.token = token;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	@Override
	public String register(String serverUrl, String userId, String token) {
		serverUrl = getMikronoServer(serverUrl);
		try {

			return restTemplate.exchange(StringUtils.chomp(serverUrl, "/") + "/rest/register", HttpMethod.POST, new HttpEntity<>(new RegisterInfo(userId, token), getSuperUserHttpHeaders(serverUrl)), String.class).getBody();
		} catch (HttpClientErrorException e) {
			throw e;
		}
	}

	@Override
	public String getMikronoServer(String serverUrl) {
		return serverUrl==null ? this.tokensPerServer.keySet().iterator().next() : serverUrl;
	}

	@Override
	public void sendMessage(String serverUrl, String username, String userToken, EmailOrSmsMessage emailOrSmsMessage) throws IOException {
		try {
			serverUrl = getMikronoServer(serverUrl);
			restTemplate.exchange(StringUtils.chomp(serverUrl,"/")+ "/rest/icure/sendMessage", HttpMethod.POST, new HttpEntity<>(emailOrSmsMessage, getUserHttpHeaders(serverUrl, username, userToken)), String.class);
		} catch (HttpClientErrorException e) {
			log.error("Error: "+e.getResponseBodyAsString(),e);
			throw new IOException(e);
		}
	}

	@Override
	public List<AppointmentDto> getAppointmentsByDate(String serverUrl, String username, String userToken, String ownerId, Long calendarDate) {
		serverUrl = getMikronoServer(serverUrl);
		ResponseEntity<MikronoAppointmentsDto> appointmentDtosResponse = restTemplate.exchange(StringUtils.chomp(serverUrl, "/") + "/rest/icure/appointmentsByDay/{userId}/{date}", HttpMethod.GET, new HttpEntity<>(null, getSuperUserHttpHeaders(serverUrl)), MikronoAppointmentsDto.class, ownerId, calendarDate);
		return appointmentDtosResponse.getBody().getAppointments().stream().map(AppointmentDto::new).collect(Collectors.toList());
	}

	@Override
	public List<AppointmentDto> getAppointmentsByPatient(String serverUrl, String username, String userToken, String ownerId, String patientId, Long startTime, Long EndTime) {
		serverUrl = getMikronoServer(serverUrl);
		ResponseEntity<MikronoAppointmentsDto> appointmentDtosResponse = restTemplate.exchange(StringUtils.chomp(serverUrl, "/") + "/rest/icure/appointments/{userId}/{patientId}", HttpMethod.GET, new HttpEntity<>(null, getSuperUserHttpHeaders(serverUrl)), MikronoAppointmentsDto.class, ownerId, patientId);
		return appointmentDtosResponse.getBody().getAppointments().stream().map(AppointmentDto::new).collect(Collectors.toList());
	}

}

/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.be.mikrono.impl

import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.taktik.icure.be.mikrono.MikronoLogic
import org.taktik.icure.dto.be.mikrono.EmailOrSmsMessage
import org.taktik.icure.services.external.rest.v1.dto.AppointmentDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.AppointmentImportDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentTypeRestDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentsDto
import java.io.IOException
import java.io.Serializable
import java.util.*

class MikronoLogicImpl(private val applicationToken: String, defaultServer: String?, defaultSuperUser: String?, defaultSuperToken: String?) : MikronoLogic {
    private val tokensPerServer: MutableMap<String, String>
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val restTemplate = RestTemplate()
    private fun getSuperUserHttpHeaders(server: String): HttpHeaders? {
        var plainCreds = tokensPerServer[server] ?: return null

        //Insert application token
        val parts = plainCreds.split(":").toTypedArray()
        val userToken = parts[1]
        plainCreds = parts[0] + ":" + getPassword(userToken)
        val plainCredsBytes = plainCreds.toByteArray()
        val base64CredsBytes = Base64.encodeBase64(plainCredsBytes)
        val base64Creds = String(base64CredsBytes)
        val headers = HttpHeaders()
        headers.add("Authorization", "Basic $base64Creds")
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }

    private fun getUserHttpHeaders(server: String?, email: String, userPassword: String): HttpHeaders {
        val plainCredsBytes = "$email:$userPassword".toByteArray()
        val base64CredsBytes = Base64.encodeBase64(plainCredsBytes)
        val base64Creds = String(base64CredsBytes)
        val headers = HttpHeaders()
        headers.add("Authorization", "Basic $base64Creds")
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }

    override fun getPassword(userToken: String): String {
        return "$applicationToken;$userToken"
    }

    internal inner class RegisterInfo : Serializable {
        var id: String? = null
        var token: String? = null

        constructor() {}
        constructor(id: String?, token: String?) {
            this.id = id
            this.token = token
        }

    }

    override fun register(serverUrl: String, userId: String?, token: String?): String? {
        return try {
            val url = getMikronoServer(serverUrl) ?: throw IllegalStateException("No mikrono server url")
            restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/register", HttpMethod.POST, HttpEntity(RegisterInfo(userId, token), getSuperUserHttpHeaders(url)), String::class.java)?.body
        } catch (e: HttpClientErrorException) {
            throw e
        }
    }

    override fun getMikronoServer(serverUrl: String?): String? {
        return serverUrl ?: tokensPerServer.keys.firstOrNull()
    }

    @Throws(IOException::class)
    override fun sendMessage(serverUrl: String?, username: String, userToken: String, emailOrSmsMessage: EmailOrSmsMessage) {
        try {
            val url = getMikronoServer(serverUrl) ?: throw IllegalStateException("No mikrono server url")
            restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/icure/sendMessage", HttpMethod.POST, HttpEntity(emailOrSmsMessage, getUserHttpHeaders(url, username, userToken)), String::class.java)
        } catch (e: HttpClientErrorException) {
            log.error("Error: " + e.responseBodyAsString, e)
            throw IOException(e)
        }
    }

    override fun getAppointmentsByDate(serverUrl: String?, username: String?, userToken: String?, ownerId: String?, calendarDate: Long?): List<AppointmentDto?> {
        val url = getMikronoServer(serverUrl) ?: throw IllegalStateException("No mikrono server url")
        val appointmentDtosResponse = restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/icure/appointmentsByDay/{userId}/{date}", HttpMethod.GET, HttpEntity<Any?>(null, getSuperUserHttpHeaders(url)), MikronoAppointmentsDto::class.java, ownerId, calendarDate)
        return appointmentDtosResponse.body?.appointments?.map { a: MikronoAppointmentDto? -> AppointmentDto(a!!) } ?: listOf()
    }

    override fun getAppointmentsByPatient(serverUrl: String?, username: String?, userToken: String?, ownerId: String?, patientId: String?, startTime: Long?, EndTime: Long?): List<AppointmentDto?> {
        val url = getMikronoServer(serverUrl) ?: throw IllegalStateException("No mikrono server url")
        val appointmentDtosResponse = restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/icure/appointments/{userId}/{patientId}", HttpMethod.GET, HttpEntity<Any?>(null, getSuperUserHttpHeaders(url)), MikronoAppointmentsDto::class.java, ownerId, patientId)
        return appointmentDtosResponse.body?.appointments?.map { a: MikronoAppointmentDto? -> AppointmentDto(a!!) } ?: listOf()
    }

    @Throws(IOException::class)
    override fun createAppointments(serverUrl: String?, username: String, userToken: String, appointments: List<AppointmentImportDto>): List<String> {
        val finalServerUrl = getMikronoServer(serverUrl)
        return appointments.mapNotNull { a: AppointmentImportDto ->
            try {
                restTemplate.exchange(StringUtils.chomp(finalServerUrl, "/") + "/rest/appointmentResource", HttpMethod.PUT, HttpEntity(a, getUserHttpHeaders(finalServerUrl, username, userToken)), String::class.java).body
            } catch (e: HttpClientErrorException) {
                if (e.statusCode == HttpStatus.FAILED_DEPENDENCY) {
                    log.error("Customer with external ID" + a.externalCustomerId + " is missing in db")
                } else {
                    log.error(e.message)
                }
                e.statusCode.toString() + ":" + e.message
            }
        }
    }

    @Throws(IOException::class)
    override fun createAppointmentTypes(serverUrl: String?, username: String, userToken: String, appointmentTypes: List<MikronoAppointmentTypeRestDto?>): List<MikronoAppointmentTypeRestDto?> {
        val finalServerUrl = getMikronoServer(serverUrl)
        return appointmentTypes.map { a: MikronoAppointmentTypeRestDto? ->
            try {
                return@map restTemplate.exchange(StringUtils.chomp(finalServerUrl, "/") + "/rest/appointmentTypeResource", HttpMethod.PUT, HttpEntity(a, getUserHttpHeaders(finalServerUrl, username, userToken)), MikronoAppointmentTypeRestDto::class.java).body
            } catch (e: HttpClientErrorException) {
                if (e.statusCode == HttpStatus.FAILED_DEPENDENCY) {
                    log.error("Error when creating appointment type: FAILED_DEPENDENCY: " + e.message)
                } else {
                    log.error(e.message)
                }
                //return e.getStatusCode()+":"+e.getMessage();
                return@map null
            }
        }
    }

    init {
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
        tokensPerServer = HashMap()
        if (defaultServer != null && defaultSuperUser != null && defaultSuperToken != null) {
            tokensPerServer[defaultServer] = java.lang.String.join(":", defaultSuperUser, defaultSuperToken)
        }
        log.info("Mikrono Logic initialised")
    }
}

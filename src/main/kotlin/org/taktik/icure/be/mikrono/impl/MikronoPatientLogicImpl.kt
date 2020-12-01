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
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.taktik.icure.be.mikrono.MikronoPatientLogic
import org.taktik.icure.be.mikrono.dto.ChangeExternalIDReplyDto
import org.taktik.icure.be.mikrono.dto.ChangeExternalIDRequestDto
import org.taktik.icure.be.mikrono.dto.ListPatientsDto
import org.taktik.icure.be.mikrono.dto.PatientDTO
import org.taktik.icure.be.mikrono.dto.kmehr.Person
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper
import org.taktik.icure.utils.FuzzyValues
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Consumer

/**
 * Created by aduchate on 16/12/11, 11:46
 */
class MikronoPatientLogicImpl(private val applicationToken: String, private val addressMapper: AddressMapper) : MikronoPatientLogic {
    private val restTemplate = RestTemplate()

    private fun getPersonFromPatient(p: Patient): Person {
        val patient = Person()
        patient.firstname = p.firstName
        patient.familyname = p.lastName
        try {
            patient.birthdate = if (p.dateOfBirth != null) Date.from(FuzzyValues.getDateTime(p.dateOfBirth.toLong()).atZone(ZoneId.systemDefault()).toInstant()) else null
        } catch (ignored: NullPointerException) {
        } catch (ignored: DateTimeException) {
        }
        if (p.gender != null) {
            patient.sex = p.gender.code
        }
        if (p.ssin != null) {
            patient.addId("ID-PATIENT:" + p.ssin)
        }
        p.addresses.forEach(Consumer { a: Address ->
            val address = addressMapper.mapToMikrono(a)
            address.zip = a.postalCode
            val addressType = if (a.addressType == null) "home" else a.addressType.name
            address.types?.add("CD-ADDRESS:$addressType")
            a.telecoms.forEach(Consumer { (telecomType, telecomNumber) ->
                patient.telecoms.add(org.taktik.icure.be.mikrono.dto.kmehr.Telecom(telecomNumber, addressType, telecomType?.name
                        ?: "email"))
            })
            patient.addAddress(address)
        })
        return patient
    }

    private fun getPatientFromDto(dto: PatientDTO): Patient? {
        val p = dto.patient

        return p?.let { pat ->
            Patient(
                    id = dto.externalId!!,
                    firstName = pat.firstname,
                    lastName = pat.familyname,
                    dateOfBirth = if (pat.birthdate != null) FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(pat.birthdate!!.toInstant(), ZoneId.systemDefault()), ChronoUnit.DAYS).toInt() else null,
                    gender = pat.sex?.let { Gender.fromCode(it)},
                    ssin = pat.getId("ID-PATIENT"),
                    addresses = pat.addresses.filterNotNull().map { a ->
                        addressMapper.map(a).let {
                            val addressType = it.addressType
                                    ?: a.types?.firstOrNull()?.let {
                                        a.types?.filter { typ -> typ?.startsWith("CD-ADDRESS:") == true }?.firstOrNull()?.replace("CD-ADDRESS:".toRegex(), "")?.let { AddressType.valueOf(it) }
                                    } ?: AddressType.home
                            it.copy(
                                    addressType = addressType,
                                    postalCode = a.zip,
                                    telecoms = p.telecoms.filter { t -> t?.location == addressType.name }.mapNotNull { t -> t?.type?.let { Telecom(telecomType = TelecomType.valueOf(it), telecomNumber = t.address) } }
                            )
                        }
                    }
                    )
        }
    }

    private fun getHttpHeaders(mikronoUser: String, mikronoPassword: String): HttpHeaders {
        val plainCreds = if (mikronoPassword.matches(Regex("[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}"))) "$mikronoUser:$applicationToken;$mikronoPassword" else "$mikronoUser:$mikronoPassword"
        val plainCredsBytes = plainCreds.toByteArray()
        val base64CredsBytes = Base64.encodeBase64(plainCredsBytes)
        val base64Creds = String(base64CredsBytes)
        val headers = HttpHeaders()
        headers.add("Authorization", "Basic $base64Creds")
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }

    override fun updatePatients(url: String?, patients: Collection<Patient>, mikronoUser: String, mikronoPassword: String) {
        for (p in patients) {
            val dto = PatientDTO()
            dto.patient = getPersonFromPatient(p)
            dto.externalId = p.id
            restTemplate.exchange(StringUtils.chomp(url, "/") + "/rest/kmehrPatientByExternalId/{externalId}", HttpMethod.PUT, HttpEntity(dto, getHttpHeaders(mikronoUser, mikronoPassword)), String::class.java, p.id)
        }
    }

    override fun createPatients(url: String?, patients: Collection<Patient>, mikronoUser: String, mikronoPassword: String): List<Long> {
        val result: MutableList<Long> = ArrayList()
        for (p in patients) {
            val dto = PatientDTO()
            dto.patient = getPersonFromPatient(p)
            dto.externalId = p.id
            restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/kmehrPatients", HttpMethod.POST, HttpEntity(dto, getHttpHeaders(mikronoUser, mikronoPassword)), Long::class.java).body?.let { result.add(it) }
        }
        return result
    }

    override fun loadPatient(url: String?, id: String?, mikronoUser: String, mikronoPassword: String): Patient? {
        return restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/kmehrPatient/{id}", HttpMethod.GET, HttpEntity<Any>(getHttpHeaders(mikronoUser, mikronoPassword)), PatientDTO::class.java, id).body?.let { getPatientFromDto(it) }
    }

    override fun updateExternalIds(url: String?, ids: Map<String, String>?, mikronoUser: String, mikronoPassword: String): ChangeExternalIDReplyDto {
        val exchange = restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/changeExternalIds", HttpMethod.PUT, HttpEntity(ChangeExternalIDRequestDto(ids), getHttpHeaders(mikronoUser, mikronoPassword)), ChangeExternalIDReplyDto::class.java)
        check(exchange.statusCode == HttpStatus.OK)
        return exchange.body ?: throw IllegalAccessException("Cannot change external ids")
    }

    override fun updatePatientId(url: String?, id: String?, externalId: String, mikronoUser: String, mikronoPassword: String) {
        restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/kmehrPatient/{id}", HttpMethod.PUT, HttpEntity(externalId, getHttpHeaders(mikronoUser, mikronoPassword)), String::class.java, id)
    }

    override fun loadPatientWithIcureId(url: String?, id: String?, mikronoUser: String, mikronoPassword: String): Patient? {
        return restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/kmehrPatientByExternalId/{id}", HttpMethod.GET, HttpEntity<Any>(getHttpHeaders(mikronoUser, mikronoPassword)), PatientDTO::class.java, id).body?.let { getPatientFromDto(it) }
    }

    override fun listPatients(url: String?, fromDate: Date?, mikronoUser: String, mikronoPassword: String): List<String> {
        return restTemplate.exchange(StringUtils.removeEnd(url, "/") + "/rest/patients/{from}", HttpMethod.GET, HttpEntity<Any>(getHttpHeaders(mikronoUser, mikronoPassword)), ListPatientsDto::class.java, (fromDate ?: Date(0L)).time).body?.patients ?: listOf()
    }

    init {
        restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
    }
}

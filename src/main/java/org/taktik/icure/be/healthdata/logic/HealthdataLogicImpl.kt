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

package org.taktik.icure.be.healthdata.logic

import be.ehealth.technicalconnector.config.ConfigFactory
import be.fgov.ehealth.etee.crypto.encrypt.DataSealerBuilder
import be.fgov.ehealth.etee.crypto.encrypt.EncryptionTokenFactory
import be.fgov.ehealth.etee.crypto.policies.EncryptionPolicy
import be.fgov.ehealth.etee.crypto.policies.OCSPPolicy
import be.fgov.ehealth.etee.crypto.policies.SigningPolicy
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import org.taktik.icure.be.ehealth.logic.crypto.impl.CryptoEteeImpl
import org.taktik.icure.be.healthdata.dto.Identifier
import org.taktik.icure.be.healthdata.dto.Registry
import org.taktik.icure.be.healthdata.dto.SendInformation
import org.taktik.icure.be.healthdata.dto.StartRegistration
import org.taktik.icure.be.healthdata.dto.StartRegistrationResponse
import java.util.*
import be.fgov.ehealth.etee.crypto.policies.SigningCredential
import be.fgov.ehealth.etee.crypto.utils.KeyManager
import org.taktik.icure.be.ehealth.dto.common.Addressee
import org.taktik.icure.be.ehealth.dto.common.Document
import org.taktik.icure.be.ehealth.dto.common.DocumentMessage
import org.taktik.icure.be.ehealth.dto.common.IdentifierType
import org.taktik.icure.be.ehealth.logic.ehealthbox.EhealthBoxLogic
import java.io.File
import java.security.PrivateKey
import java.security.cert.X509Certificate


class HealthdataLogicImpl {
	private val config = ConfigFactory.getConfigValidator(listOf())
	val restTemplate = RestTemplate()

	var cryptoEteeImpl : CryptoEteeImpl? = null
	var eHealthBoxLogic : EhealthBoxLogic? = null

	fun consultCatalogue() = restTemplate.exchange("https://catalogue.healthdata.be/healthdata_catalogue/catalogue/api/v1/registries", HttpMethod.GET, null, object : ParameterizedTypeReference<List<Registry>>() {}).body
	fun startRegistration(user: String, pass: String, id: String, version: String, iCureVersion: String): StartRegistrationResponse = restTemplate.exchange("https://hd4prc-acc.healthdata.be/healthdata_hd4prc/cloud/api/v1/registrations", HttpMethod.POST,
		HttpEntity(StartRegistration(id = Identifier(id, version), integrator = Identifier("iCure", iCureVersion)),
			HttpHeaders().apply {
				add(CONTENT_TYPE, "application/json")
				add(ACCEPT_LANGUAGE, "fr-BE")
				add(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).toByteArray(Charsets.UTF_8)))
			}), StartRegistrationResponse::class.java).body

	fun uploadRegistrationData(user: String, pass: String, registrationId: String, sumehr: String) = restTemplate.exchange("https://hd4prc-acc.healthdata.be/healthdata_hd4prc/cloud/api/v1/registrations/$registrationId/data-set", HttpMethod.POST,
		HttpEntity(sumehr,
			HttpHeaders().apply {
				add(CONTENT_TYPE, "application/vnd.healthconnect.eforms.kmehr.integrator.v1+xml;charset=UTF-8")
				add(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).toByteArray(Charsets.UTF_8)))
			}), Unit::class.java).let {
		it.statusCode == HttpStatus.ACCEPTED || it.statusCode == HttpStatus.NO_CONTENT
	}

	fun exportRegistrationCsv(user: String, pass: String, registrationId: String) = restTemplate.exchange("https://hd4prc-acc.healthdata.be/healthdata_hd4prc/cloud/api/v1/registrations/$registrationId/csv ", HttpMethod.GET,
		HttpEntity(null,
			HttpHeaders().apply {
				add(ACCEPT, "text/csv")
				add(ACCEPT_LANGUAGE, "fr-BE")
				add(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).toByteArray(Charsets.UTF_8)))
			}), String::class.java).body

	fun collectForSending(user: String, pass: String, registrationId: String) = restTemplate.exchange("https://hd4prc-acc.healthdata.be/healthdata_hd4prc/cloud/api/v1/registrations/$registrationId/send ", HttpMethod.POST,
		HttpEntity(null,
			HttpHeaders().apply {
				add(ACCEPT, "application/json")
				add(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).toByteArray(Charsets.UTF_8)))
			}), SendInformation::class.java).body

	fun encryptAndSend(user: String, pass: String, token: String, keystorePassword: String, registrationId: String, sendInformation: SendInformation): Boolean {
		val keystoreInputStream = File(config.getProperty("KEYSTORE_DIR"), config.getProperty("sessionmanager.identification.keystore")).inputStream()
		val keyStore = KeyManager.getKeyStore(keystoreInputStream, "PKCS12", keystorePassword.toCharArray())
		val privateKey = keyStore.getKey("authentication", keystorePassword.toCharArray()) as PrivateKey
		val certificate = keyStore.getCertificate("authentication") as X509Certificate
		val signingCredential = SigningCredential.create(privateKey, certificate)
		val dataSealer = DataSealerBuilder.newBuilder().addOCSPPolicy(OCSPPolicy.NONE) .addSigningPolicy(SigningPolicy.EHEALTH_CERT, signingCredential).addPublicKeyPolicy(EncryptionPolicy.KNOWN_RECIPIENT).addSecretKeyPolicy(EncryptionPolicy.UNKNOWN_RECIPIENT).build()
		val etk = EncryptionTokenFactory.getInstance().create(cryptoEteeImpl!!.fetchEtk(sendInformation.encryptionAddressee.identificationValue, sendInformation.encryptionAddressee.identificationType, sendInformation.encryptionAddressee.applicationId))

		val csvContent = sendInformation.csvContent.split(Regex("\r\n|\r|\n")).map { line ->
			val fields = line.split(";").toMutableList()
			fields[fields.lastIndex] = fields.last().let {
				Base64.getEncoder().encodeToString(dataSealer.seal(etk, Base64.getDecoder().decode(it)))
			}
			fields.joinToString(";")
		}.joinToString("\r\n")


		eHealthBoxLogic!!.sendMessage(token, DocumentMessage().apply {
			document = Document().apply {
				title = sendInformation.subject
			}
			destinations = listOf(Addressee().apply { identifierType = IdentifierType.CBE; id = "0809394427"; applicationId = "TTP"; quality ="INSTITUTION" })
			customMetas = sendInformation.metadata
			annex = listOf(Document().apply { content = csvContent.toByteArray(Charsets.UTF_8); mimeType = "text/csv" })
		}, 0)
		return true
    }
}

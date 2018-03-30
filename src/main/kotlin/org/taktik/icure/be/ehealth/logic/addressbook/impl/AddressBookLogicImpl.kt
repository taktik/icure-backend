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

package org.taktik.icure.be.ehealth.logic.addressbook.impl

import be.ehealth.businessconnector.addressbook.session.AddressbookSessionServiceFactory
import be.ehealth.technicalconnector.exception.ConnectorException
import be.fgov.ehealth.addressbook.core.v1.IndividualContactInformationType
import be.fgov.ehealth.addressbook.protocol.v1.SearchProfessionalsRequest
import org.joda.time.DateTime
import org.taktik.icure.be.ehealth.logic.addressbook.AddressBookLogic
import org.taktik.icure.entities.HealthcareParty
import be.fgov.ehealth.addressbook.protocol.v1.GetProfessionalContactInfoRequest
import be.fgov.ehealth.addressbook.protocol.v1.SearchOrganizationsRequest
import org.springframework.stereotype.Service
import org.taktik.icure.entities.embed.*


/**
 * Created by aduchate on 06/04/2017.
 */
@Service
class AddressBookLogicImpl : AddressBookLogic {
    override fun searchHealthcareParties(queryLastName: String, queryFirstName: String?): List<HealthcareParty> {
        try {
            if (queryLastName.length < 2 || queryFirstName?.length ?: 0 < 1)  { return emptyList() }
            val searchProfessionals = AddressbookSessionServiceFactory.getAddressbookSessionService().searchProfessionals(SearchProfessionalsRequest().apply {
                firstName = queryFirstName; lastName = queryLastName; issueInstant = DateTime.now(); profession = "PHYSICIAN"
                offset = 0; maxElements = 100;
            })
            return searchProfessionals.healthCareProfessionals?.filter { it.firstName != null && it.firstName.startsWith(queryFirstName!!,true)}?.map { HealthcareParty().apply {
                firstName = it.firstName
                lastName = it.lastName
                ssin = it.ssin
                nihii = (it.professions.find { it.professionCodes.any { it.value == "PHYSICIAN" } } ?: it.professions.firstOrNull())?.let { it.nihii }
                gender = Gender.fromCode(it.gender)
            } } ?: listOf()
        } catch (e: ConnectorException) {
            throw IllegalStateException(e)
        }

    }

	override fun searchOrganisations(name: String, language: String): List<HealthcareParty> {
		try {
			val searchOrganizations = AddressbookSessionServiceFactory.getAddressbookSessionService().searchOrganizations(SearchOrganizationsRequest().apply {
				institutionName = name; institutionType = "HOSPITAL"; issueInstant = DateTime.now()
				offset = 0; maxElements = 100;
			})
			return searchOrganizations.healthCareOrganizations?.map { HealthcareParty().apply {
				this.name = (it.names.find{ it.lang == language } ?: it.names.firstOrNull())?.value
				nihii = it.id.value + "000"
			} } ?: listOf()
		} catch (e: ConnectorException) {
			throw IllegalStateException(e)
		}

	}

	override fun getHealthcarePartyByNihii(queryNihii: String, language: String): HealthcareParty {
        val professionalContactInfo = AddressbookSessionServiceFactory.getAddressbookSessionService().getProfessionalContactInfo(GetProfessionalContactInfoRequest().apply { nihii = queryNihii; issueInstant = DateTime.now() })
        val it = professionalContactInfo.individualContactInformation
        return makeHealthcareParty(it, language)
    }

    override fun getHealthcarePartyBySsin(querySsin: String, language: String): HealthcareParty {
        val professionalContactInfo = AddressbookSessionServiceFactory.getAddressbookSessionService().getProfessionalContactInfo(GetProfessionalContactInfoRequest().apply { ssin = querySsin; issueInstant = DateTime.now() })
        val it = professionalContactInfo.individualContactInformation
        return makeHealthcareParty(it, language)
    }

    private fun makeHealthcareParty(it: IndividualContactInformationType, language: String) : HealthcareParty {
        return HealthcareParty().apply {
            firstName = it.firstName
            lastName = it.lastName
            ssin = it.ssin
            gender = Gender.fromCode(it.gender)
            val professionalInformation = it.professionalInformations.find { it.profession?.professionCodes?.any { it.value == "PHYSICIAN" } ?: false } ?: it.professionalInformations.firstOrNull()
            nihii = professionalInformation?.let { it.profession.nihii }
            addresses = professionalInformation?.addresses?.map {
                Address().apply {
                    addressType = AddressType.work
                    street = (it.street.descriptions.find { it.lang == language } ?: it.street.descriptions.firstOrNull())?.value
                    houseNumber = it.houseNumber
                    postboxNumber = it.postBox
                    country = (it.country.descriptions.find { it.lang == language } ?: it.country.descriptions.firstOrNull())?.value
                    postalCode = it.municipality?.zipCode?.toString()
                    city = (it.municipality.descriptions.find { it.lang == language } ?: it.municipality.descriptions.firstOrNull())?.value
                    telecoms = professionalInformation.healthCareAdditionalInformations?.filter { it.type == "Mail" }?.map { Telecom().apply { telecomType = if (it.type == "Mail") TelecomType.email else null; telecomNumber = it.value } }?.toHashSet() ?: hashSetOf()
                }
            }?.toHashSet() ?: hashSetOf()
        }
    }
}

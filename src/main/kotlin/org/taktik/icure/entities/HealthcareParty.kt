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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.CryptoActor
import org.taktik.icure.entities.base.Person
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.*
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.entities.utils.MergeUtil.mergeMapsOfArraysDistinct
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.ValidCode
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList
import java.util.function.BiFunction

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class HealthcareParty(id: String,
                      rev: String? = null,
                      revisionsInfo: Array<RevisionInfo> = arrayOf(),
                      conflicts: Array<String> = arrayOf(),
                      revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory), Person, CryptoActor {
    var name: String? = null
    override var lastName: String? = null
    override var firstName: String? = null
    override var gender: Gender? = null
    override var civility: String? = null
    var speciality: String? = null
    var companyName: String? = null
    var bankAccount: String? = null
    var bic: String? = null
    var proxyBankAccount: String? = null
    var proxyBic: String? = null
    var invoiceHeader: String? = null
    var cbe: String? = null
    var ehp: String? = null
    var userId: String? = null
    var parentId: String? = null
    var convention //0,1,2,9
            : Int? = null
    var supervisorId: String? = null
    var nihii //institution, person
            : String? = null
    var nihiiSpecCode //don't show field in the GUI
            : String? = null
    var ssin: String? = null
    override var addresses: MutableList<Address> = LinkedList()
    override var languages: MutableList<String> = LinkedList()
    var picture: ByteArray? = null
    var statuses: List<HealthcarePartyStatus>? = null

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    var specialityCodes //Speciality codes, default is first
            : List<CodeStub>? = null
    var sendFormats: Map<TelecomType, String>? = null
    var notes: String? = null

    //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
    //For a pair of HcParties, this key is called the AES exchange key
    //Each HcParty always has one AES exchange key for himself
    // The map's keys are the delegate id.
    // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
    // the key encrypted using delegate's public key.
    override var hcPartyKeys: Map<String, Array<String>> = HashMap()
    var privateKeyShamirPartitions: Map<String, String> = HashMap() //Format is hcpId of key that has been partitionned : "threshold|partition in hex"
    var financialInstitutionInformation: MutableList<FinancialInstitutionInformation> = ArrayList()
    var options: Map<String, String> = HashMap()
    override var publicKey: String? = null

    // Medical houses
    var billingType // "serviceFee" (à l'acte) or "flatRate" (forfait)
            : String? = null
    var type // "persphysician" or "medicalHouse" or "perstechnician"
            : String? = null
    var contactPerson: String? = null
    var contactPersonHcpId: String? = null
    var flatRateTarifications: List<FlatRateTarification>? = null
    var importedData: Map<String, String> = HashMap()

    @get:JsonIgnore
    val fullName: String?
        get() {
            var full: String?
            full = lastName
            if (firstName != null) {
                full = if (full != null) "$full $firstName" else firstName
            }
            return full
        }

    fun solveConflictWith(other: HealthcareParty): HealthcareParty {
        super.solveConflictsWith(other)
        mergeFrom(other)
        return this
    }

    fun mergeFrom(other: HealthcareParty) {
        if (name == null && other.name != null) {
            name = other.name
        }
        if (firstName == null && other.firstName != null) {
            firstName = other.firstName
        }
        if (lastName == null && other.lastName != null) {
            lastName = other.lastName
        }
        if (ssin == null && other.ssin != null) {
            ssin = other.ssin
        }
        if (civility == null && other.civility != null) {
            civility = other.civility
        }
        if (gender == null && other.gender != null && other.gender !== Gender.unknown) {
            gender = other.gender
        }
        if (publicKey == null && other.publicKey != null) {
            publicKey = other.publicKey
        }
        if (picture == null && other.picture != null) {
            picture = other.picture
        }
        if (notes == null && other.notes != null) {
            notes = other.notes
        }
        hcPartyKeys = mergeMapsOfArraysDistinct(hcPartyKeys, other.hcPartyKeys, BiFunction { obj: String, anObject: String? -> obj.equals(anObject) }, BiFunction { a: String, b: String? -> a })
        languages = mergeListsDistinct(languages, other.languages, { obj: String?, anotherString: String? -> obj.equals(anotherString, ignoreCase = true) }, { a: String, b: String? -> a }).toMutableList()
        if (speciality == null && other.speciality != null) {
            speciality = other.speciality
        }
        if (companyName == null && other.companyName != null) {
            companyName = other.companyName
        }
        if (bankAccount == null && other.bankAccount != null) {
            bankAccount = other.bankAccount
        }
        if (bic == null && other.bic != null) {
            bic = other.bic
        }
        if (proxyBankAccount == null && other.proxyBankAccount != null) {
            proxyBankAccount = other.proxyBankAccount
        }
        if (proxyBic == null && other.proxyBic != null) {
            proxyBic = other.proxyBic
        }
        if (invoiceHeader == null && other.invoiceHeader != null) {
            invoiceHeader = other.invoiceHeader
        }
        if (cbe == null && other.cbe != null) {
            cbe = other.cbe
        }
        if (ehp == null && other.ehp != null) {
            ehp = other.ehp
        }
        if (userId == null && other.userId != null) {
            userId = other.userId
        }
        if (parentId == null && other.parentId != null) {
            parentId = other.parentId
        }
        if (convention == null && other.convention != null) {
            convention = other.convention
        }
        if (supervisorId == null && other.supervisorId != null) {
            supervisorId = other.supervisorId
        }
        if (nihii == null && other.nihii != null) {
            nihii = other.nihii
        }
        if (nihiiSpecCode == null && other.nihiiSpecCode != null) {
            nihiiSpecCode = other.nihiiSpecCode
        }
        if (billingType == null && other.billingType != null) {
            billingType = other.billingType
        }
        if (type == null && other.type != null) {
            type = other.type
        }
        if (contactPerson == null && other.contactPerson != null) {
            contactPerson = other.contactPerson
        }
        if (contactPersonHcpId == null && other.contactPersonHcpId != null) {
            contactPersonHcpId = other.contactPersonHcpId
        }
        statuses = mergeListsDistinct(statuses, other.statuses, { obj: HealthcarePartyStatus, other: HealthcarePartyStatus? -> obj.equals(other) }, { a: HealthcarePartyStatus, b: HealthcarePartyStatus? -> a })
        for (fromAddress in other.addresses) {
            val destAddress = addresses.stream().filter { address: Address -> address.addressType === fromAddress.addressType }.findAny()
            if (destAddress.isPresent) {
                destAddress.orElseThrow { IllegalStateException() }.mergeFrom(fromAddress)
            } else {
                addresses.add(fromAddress)
            }
        }
        for (fromFinancialInstitutionInformation in other.financialInstitutionInformation) {
            val destFinancialInstitutionInformation = financialInstitutionInformation.stream().filter { financialInstitutionInformation: FinancialInstitutionInformation -> financialInstitutionInformation.bankAccount === fromFinancialInstitutionInformation.bankAccount }.findAny()
            if (!destFinancialInstitutionInformation.isPresent) {
                financialInstitutionInformation.add(fromFinancialInstitutionInformation)
            }
        }
    }

    fun forceMergeFrom(other: HealthcareParty) {
        if (other.firstName != null) {
            firstName = other.firstName
        }
        if (other.lastName != null) {
            lastName = other.lastName
        }
        if (other.ssin != null) {
            ssin = other.ssin
        }
        if (other.civility != null) {
            civility = other.civility
        }
        if (other.gender != null && other.gender !== Gender.unknown) {
            gender = other.gender
        }
        forceMergeAddresses(other.addresses)
    }

    fun forceMergeAddresses(otherAddresses: List<Address>) {
        for (fromAddress in otherAddresses) {
            val destAddress = addresses.stream().filter { address: Address -> address.addressType === fromAddress.addressType }.findAny()
            if (destAddress.isPresent) {
                destAddress.orElseThrow { IllegalStateException() }.forceMergeFrom(fromAddress)
            } else {
                addresses.add(fromAddress)
            }
        }
    }

}

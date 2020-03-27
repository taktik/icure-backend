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
import org.apache.commons.codec.digest.DigestUtils
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.CryptoActor
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.Person
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.DeactivationReason
import org.taktik.icure.entities.embed.EmploymentInfo
import org.taktik.icure.entities.embed.FinancialInstitutionInformation
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Insurability
import org.taktik.icure.entities.embed.MedicalHouseContract
import org.taktik.icure.entities.embed.Partnership
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.PersonalStatus
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.entities.embed.SchoolingInfo
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.entities.utils.MergeUtil.mergeMapsOfArraysDistinct
import org.taktik.icure.entities.utils.MergeUtil.mergeSets
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.ValidCode
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.TreeSet
import java.util.function.BiFunction

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Patient : StoredICureDocument(), Person, Encryptable, CryptoActor {
    var mergeToPatientId: String? = null
    var mergedIds: Set<String> = HashSet()
    var nonDuplicateIds: Set<String> = HashSet()
    var encryptedAdministrativesDocuments: Set<String> = HashSet()
    override var firstName: String? = null
    override var lastName //Is usually either maidenName or spouseName
            : String? = null
    var alias: String? = null
    var active = true
    var deactivationReason = DeactivationReason.none
    var ssin: String? = null
    override var civility: String? = null
    override var gender: Gender? = Gender.unknown
    var maidenName // Never changes (nom de jeune fille)
            : String? = null
    var spouseName // Name of the spouse after marriage
            : String? = null
    var partnerName // Name of the partner, sometimes equal to spouseName
            : String? = null
    var personalStatus: PersonalStatus? = PersonalStatus.unknown
    var dateOfBirth // YYYYMMDD if unknown, 00, ex:20010000 or
            : Int? = null
    var dateOfDeath // YYYYMMDD if unknown, 00, ex:20010000 or
            : Int? = null
    var placeOfBirth: String? = null
    var placeOfDeath: String? = null
    var education: String? = null
    var profession: String? = null
    var note: String? = null
    var administrativeNote: String? = null
    var comment: String? = null
    var warning: String? = null
    var nationality: String? = null
    var preferredUserId: String? = null
    var picture: ByteArray?

    //No guarantee of unicity
    var externalId: String? = null
    override var addresses: MutableList<Address> = ArrayList()
    var insurabilities: List<Insurability> = ArrayList()
    override var languages: List<String> = ArrayList() //alpha-2 code http://www.loc.gov/standards/iso639-2/ascii_8bits.html
    var partnerships: MutableList<Partnership> = ArrayList()
    var patientHealthCareParties: List<PatientHealthCareParty> = ArrayList()
    var financialInstitutionInformation: MutableList<FinancialInstitutionInformation> = ArrayList()
    var medicalHouseContracts: MutableList<MedicalHouseContract> = ArrayList()
    var parameters: Map<String, List<String>> = HashMap()

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    var patientProfessions: List<CodeStub> = ArrayList()

    //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
    //For a pair of HcParties, this key is called the AES exchange key
    //Each HcParty always has one AES exchange key for himself
    // The map's keys are the delegate id.
    // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
    // the key encrypted using delegate's public key.
    override var hcPartyKeys: Map<String, Array<String>> = HashMap()
    override var publicKey: String? = null
    var fatherBirthCountry: CodeStub? = null
    var birthCountry: CodeStub? = null
    var nativeCountry: CodeStub? = null
    var socialStatus: CodeStub? = null
    var mainSourceOfIncome: CodeStub? = null
    var schoolingInfos: MutableList<SchoolingInfo> = ArrayList()
    var employementInfos: MutableList<EmploymentInfo> = ArrayList()
    var properties: Set<Property> = HashSet()

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (id == null) 0 else id.hashCode()
        result = (prime * result
                + if (dateOfBirth == null) 0 else dateOfBirth.hashCode())
        result = (prime * result
                + if (firstName == null) 0 else firstName.hashCode())
        result = (prime * result
                + if (lastName == null) 0 else lastName.hashCode())
        result = prime * result + if (ssin == null) 0 else ssin.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Patient
        if (id != null) {
            if (id == other.id) return true
        }
        if (dateOfBirth == null) {
            if (other.dateOfBirth != null) return false
        } else if (dateOfBirth != other.dateOfBirth) return false
        if (firstName == null) {
            if (other.firstName != null) return false
        } else if (firstName != other.firstName) return false
        if (lastName == null) {
            if (other.lastName != null) return false
        } else if (lastName != other.lastName) return false
        if (ssin == null) {
            if (other.ssin != null) return false
        } else if (ssin != other.ssin) return false
        return true
    }

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

    @get:JsonIgnore
    val signature: String
        get() = DigestUtils.md5Hex(
                "" + firstName + ":" + lastName + ":" + patientHealthCareParties.stream().filter(PatientHealthCareParty::isReferral).findFirst().map { phcp: PatientHealthCareParty -> "" + phcp.healthcarePartyId + phcp.referralPeriods.last().startDate + phcp.referralPeriods.last().endDate }.orElse("")
                        + ":" + dateOfBirth + ":" + dateOfDeath + ":" + ssin)

    fun solveConflictWith(other: Patient): Patient {
        super.solveConflictsWith(other)
        mergeFrom(other)
        return this
    }

    fun mergeFrom(other: Patient) {
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
        if (maidenName == null && other.maidenName != null) {
            maidenName = other.maidenName
        }
        if (spouseName == null && other.spouseName != null) {
            spouseName = other.spouseName
        }
        if (partnerName == null && other.partnerName != null) {
            partnerName = other.partnerName
        }
        if (personalStatus == null && other.personalStatus != null) {
            personalStatus = other.personalStatus
        }
        if (dateOfBirth == null && other.dateOfBirth != null) {
            dateOfBirth = other.dateOfBirth
        }
        if (dateOfDeath == null && other.dateOfDeath != null) {
            dateOfDeath = other.dateOfDeath
        }
        if (placeOfBirth == null && other.placeOfBirth != null) {
            placeOfBirth = other.placeOfBirth
        }
        if (placeOfDeath == null && other.placeOfDeath != null) {
            placeOfDeath = other.placeOfDeath
        }
        if (education == null && other.education != null) {
            education = other.education
        }
        if (profession == null && other.profession != null) {
            profession = other.profession
        }
        if (note == null && other.note != null) {
            note = other.note
        }
        if (nationality == null && other.nationality != null) {
            nationality = other.nationality
        }
        if (picture == null && other.picture != null) {
            picture = other.picture
        }
        if (externalId == null && other.externalId != null) {
            externalId = other.externalId
        }
        if (comment != null && other.comment != null) {
            comment = other.comment
        }
        if (alias == null && other.alias != null) {
            alias = other.alias
        }
        if (administrativeNote == null || administrativeNote!!.trim { it <= ' ' } == "" && other.administrativeNote != null) {
            administrativeNote = other.administrativeNote
        }
        if (warning == null && other.warning != null) {
            warning = other.warning
        }
        if (publicKey == null && other.publicKey != null) {
            publicKey = other.publicKey
        }
        hcPartyKeys = mergeMapsOfArraysDistinct(hcPartyKeys, other.hcPartyKeys, BiFunction { obj: String, anObject: String? -> obj.equals(anObject) }, BiFunction { a: String, b: String? -> a })
        languages = mergeListsDistinct(languages, other.languages, BiFunction { obj: String, anotherString: String? -> obj.equals(anotherString, ignoreCase = true) }, BiFunction { a: String, b: String? -> a })
        insurabilities = mergeListsDistinct(insurabilities, other.insurabilities,
                BiFunction { a: Insurability?, b: Insurability? -> a == null && b == null || a != null && b != null && a.insuranceId == b.insuranceId && a.startDate == b.startDate },
                BiFunction { a: Insurability, b: Insurability? -> if (a.endDate != null) a else b }
        )
        patientHealthCareParties = mergeListsDistinct(patientHealthCareParties, other.patientHealthCareParties,
                BiFunction { a: PatientHealthCareParty?, b: PatientHealthCareParty? -> a == null && b == null || a != null && b != null && a.healthcarePartyId == b.healthcarePartyId && a.type == b.type },
                BiFunction { a: PatientHealthCareParty, b: PatientHealthCareParty ->
                    a.referralPeriods = mergeSets(a.referralPeriods, b.referralPeriods, TreeSet(),
                            BiFunction { aa: ReferralPeriod?, bb: ReferralPeriod? -> aa == null && bb == null || aa != null && bb != null && aa.startDate == bb.startDate },
                            BiFunction { aa: ReferralPeriod, bb: ReferralPeriod ->
                                if (aa.endDate == null) {
                                    aa.endDate = bb.endDate
                                }
                                aa
                            }
                    )
                    a
                })
        patientProfessions = mergeListsDistinct(patientProfessions, other.patientProfessions, BiFunction { a: CodeStub?, b: CodeStub? -> a == b }, BiFunction { a: CodeStub, b: CodeStub? -> a })
        for (fromAddress in other.addresses) {
            val destAddress = addresses.stream().filter { address: Address -> address.addressType === fromAddress.addressType }.findAny()
            if (destAddress.isPresent) {
                destAddress.orElseThrow { IllegalStateException() }.mergeFrom(fromAddress)
            } else {
                addresses.add(fromAddress)
            }
        }

        //insurabilities
        for (fromInsurability in other.insurabilities) {
            val destInsurability = insurabilities.stream().filter { insurability: Insurability -> insurability.insuranceId == fromInsurability.insuranceId }.findAny()
            if (!destInsurability.isPresent) {
                insurabilities.add(fromInsurability)
            }
        }
        //Todo: cleanup insurabilities (enddates ...)

        //medicalhousecontracts
        for (fromMedicalHouseContract in other.medicalHouseContracts) {
            val destMedicalHouseContract = medicalHouseContracts.stream().filter { medicalHouseContract: MedicalHouseContract -> medicalHouseContract.mmNihii != null && medicalHouseContract.mmNihii == fromMedicalHouseContract.mmNihii }.findAny()
            if (!destMedicalHouseContract.isPresent) {
                medicalHouseContracts.add(fromMedicalHouseContract)
            }
        }
        for (fromLanguage in other.languages) {
            val destLanguage = languages.stream().filter { language: String -> language === fromLanguage }.findAny()
            if (!destLanguage.isPresent) {
                languages.add(fromLanguage)
            }
        }
        for (fromPartnership in other.partnerships) {
            //Todo: check comparision:
            val destPartnership = partnerships.stream().filter { partnership: Partnership -> partnership.partnerId === fromPartnership.partnerId }.findAny()
            if (!destPartnership.isPresent) {
                partnerships.add(fromPartnership)
            }
        }
        for (fromPatientHealthCareParty in other.patientHealthCareParties) {
            val destPatientHealthCareParty = patientHealthCareParties.stream().filter { patientHealthCareParty: PatientHealthCareParty -> patientHealthCareParty.healthcarePartyId === fromPatientHealthCareParty.healthcarePartyId }.findAny()
            if (!destPatientHealthCareParty.isPresent) {
                patientHealthCareParties.add(fromPatientHealthCareParty)
            }
        }
        for (fromFinancialInstitutionInformation in other.financialInstitutionInformation) {
            val destFinancialInstitutionInformation = financialInstitutionInformation.stream().filter { financialInstitutionInformation: FinancialInstitutionInformation -> financialInstitutionInformation.bankAccount === fromFinancialInstitutionInformation.bankAccount }.findAny()
            if (!destFinancialInstitutionInformation.isPresent) {
                financialInstitutionInformation.add(fromFinancialInstitutionInformation)
            }
        }
        for (fromSchoolingInfos in other.schoolingInfos) {
            val destSchoolingInfos = schoolingInfos.stream().filter { schoolingInfos: SchoolingInfo -> schoolingInfos.startDate === fromSchoolingInfos.startDate }.findAny()
            if (!destSchoolingInfos.isPresent) {
                schoolingInfos.add(fromSchoolingInfos)
            }
        }
        for (fromEmploymentInfos in other.employementInfos) {
            val destEmploymentInfos = employementInfos.stream().filter { employmentInfos: EmploymentInfo -> employmentInfos.startDate === fromEmploymentInfos.startDate }.findAny()
            if (!destEmploymentInfos.isPresent) {
                employementInfos.add(fromEmploymentInfos)
            }
        }
    }

    fun forceMergeFrom(other: Patient) {
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
        if (other.maidenName != null) {
            maidenName = other.maidenName
        }
        if (other.spouseName != null) {
            spouseName = other.spouseName
        }
        if (other.partnerName != null) {
            partnerName = other.partnerName
        }
        if (other.personalStatus != null) {
            personalStatus = other.personalStatus
        }
        if (other.dateOfBirth != null) {
            dateOfBirth = other.dateOfBirth
        }
        if (other.dateOfDeath != null) {
            dateOfDeath = other.dateOfDeath
        }
        if (other.placeOfBirth != null) {
            placeOfBirth = other.placeOfBirth
        }
        if (other.placeOfDeath != null) {
            placeOfDeath = other.placeOfDeath
        }
        if (other.education != null) {
            education = other.education
        }
        if (other.profession != null) {
            profession = other.profession
        }
        if (other.note != null) {
            note = other.note
        }
        if (other.nationality != null) {
            nationality = other.nationality
        }
        if (other.picture != null) {
            picture = other.picture
        }
        if (other.externalId != null) {
            externalId = other.externalId
        }
        if (other.comment != null) {
            comment = other.comment
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

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.*
import org.taktik.icure.entities.embed.*
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Patient(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        @NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = setOf(),
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = setOf(),
        override val endOfLife: Long? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        override val firstName : String? = null,
        override val lastName : String? = null, //Is usually either maidenName or spouseName,
        override val companyName: String?,
        override val languages: List<String> = listOf(), //alpha-2 code http://www.loc.gov/standards/iso639-2/ascii_8bits.html,
        override val addresses: List<Address> = listOf(),
        override val civility: String? = null,
        override val gender: Gender? = Gender.unknown,

        val mergeToPatientId: String? = null,
        val mergedIds: Set<String> = HashSet(),
        val nonDuplicateIds: Set<String> = HashSet(),
        val encryptedAdministrativesDocuments: Set<String> = HashSet(),
        val alias: String? = null,
        val active: Boolean = true,
        val deactivationReason: DeactivationReason = DeactivationReason.none,
        val ssin: String? = null,
        val maidenName : String? = null,// Never changes (nom de jeune fille),
        val spouseName : String? = null, // Name of the spouse after marriage,
        val partnerName : String? = null, // Name of the partner, sometimes equal to spouseName,
        val personalStatus: PersonalStatus? = PersonalStatus.unknown,
        val dateOfBirth : Int? = null, // YYYYMMDD if unknown, 00, ex:20010000 or,
        val dateOfDeath : Int? = null, // YYYYMMDD if unknown, 00, ex:20010000 or,
        val placeOfBirth: String? = null,
        val placeOfDeath: String? = null,
        val education: String? = null,
        val profession: String? = null,
        val note: String? = null,
        val administrativeNote: String? = null,
        val comment: String? = null,
        val warning: String? = null,
        val nationality: String? = null,
        val preferredUserId: String? = null,
        val picture: ByteArray? = null,

        //No guarantee of unicity
        val externalId: String? = null,
        val insurabilities: List<Insurability> = listOf(),
        val partnerships: List<Partnership> = listOf(),
        val patientHealthCareParties: List<PatientHealthCareParty> = listOf(),
        val financialInstitutionInformation: List<FinancialInstitutionInformation> = listOf(),
        val medicalHouseContracts: List<MedicalHouseContract> = listOf(),
        val parameters: Map<String, List<String>> = mapOf(),

        @ValidCode(autoFix = AutoFix.NORMALIZECODE)
        val patientProfessions: List<CodeStub> = listOf(),

        val fatherBirthCountry: CodeStub? = null,
        val birthCountry: CodeStub? = null,
        val nativeCountry: CodeStub? = null,
        val socialStatus: CodeStub? = null,
        val mainSourceOfIncome: CodeStub? = null,
        val schoolingInfos: List<SchoolingInfo> = listOf(),
        val employementInfos: List<EmploymentInfo> = listOf(),
        val properties: Set<Property> = HashSet(),

        // One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
        // For a pair of HcParties, this key is called the AES exchange key
        // Each HcParty always has one AES exchange key for himself
        // The map's keys are the delegate id.
        // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
        // the key encrypted using delegate's public key.
        override val hcPartyKeys: Map<String, Array<String>> = HashMap(),
        override val privateKeyShamirPartitions: Map<String, String>,
        override val publicKey: String? = null,

        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<Delegation>> = mapOf(),
        override val delegations: Map<String, Set<Delegation>> = mapOf(),
        override val encryptionKeys: Map<String, Set<Delegation>> = mapOf(),
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = Patient::javaClass.name
) : StoredDocument, ICureDocument, Person, Encryptable, CryptoActor {
    companion object : DynamicInitializer<Patient>
    fun merge(other: Patient) = Patient(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Patient) =
                    super<StoredDocument>.solveConflictsWith(other) +
                    super<ICureDocument>.solveConflictsWith(other) +
                    super<Person>.solveConflictsWith(other) +
                    super<Encryptable>.solveConflictsWith(other) +
                    super<CryptoActor>.solveConflictsWith(other) + mapOf(
                            "mergeToPatientId" to (this.mergeToPatientId ?: other.mergeToPatientId),
                            "mergedIds" to (other.mergedIds + this.mergedIds),
                            "nonDuplicateIds" to (other.nonDuplicateIds + this.nonDuplicateIds),
                            "encryptedAdministrativesDocuments" to (other.encryptedAdministrativesDocuments + this.encryptedAdministrativesDocuments),
                            "alias" to (this.alias ?: other.alias),
                            "active" to (this.active),
                            "deactivationReason" to (this.deactivationReason),
                            "ssin" to (this.ssin ?: other.ssin),
                            "maidenName" to (this.maidenName ?: other.maidenName),
                            "spouseName" to (this.spouseName ?: other.spouseName),
                            "partnerName" to (this.partnerName ?: other.partnerName),
                            "personalStatus" to (this.personalStatus ?: other.personalStatus),
                            "dateOfBirth" to (this.dateOfBirth ?: other.dateOfBirth),
                            "dateOfDeath" to (this.dateOfDeath ?: other.dateOfDeath),
                            "placeOfBirth" to (this.placeOfBirth ?: other.placeOfBirth),
                            "placeOfDeath" to (this.placeOfDeath ?: other.placeOfDeath),
                            "education" to (this.education ?: other.education),
                            "profession" to (this.profession ?: other.profession),
                            "note" to (this.note ?: other.note),
                            "administrativeNote" to (this.administrativeNote ?: other.administrativeNote),
                            "comment" to (this.comment ?: other.comment),
                            "warning" to (this.warning ?: other.warning),
                            "nationality" to (this.nationality ?: other.nationality),
                            "preferredUserId" to (this.preferredUserId ?: other.preferredUserId),
                            "picture" to (this.picture ?: other.picture),
                            "externalId" to (this.externalId ?: other.externalId),
                            "partnerships" to mergeListsDistinct(partnerships, other.partnerships),
                            "financialInstitutionInformation" to mergeListsDistinct(financialInstitutionInformation, other.financialInstitutionInformation),
                            "medicalHouseContracts" to mergeListsDistinct(medicalHouseContracts, other.medicalHouseContracts),
                            "parameters" to (other.parameters + this.parameters),
                            "patientProfessions" to mergeListsDistinct(patientProfessions, other.patientProfessions),
                            "fatherBirthCountry" to (this.fatherBirthCountry ?: other.fatherBirthCountry),
                            "birthCountry" to (this.birthCountry ?: other.birthCountry),
                            "nativeCountry" to (this.nativeCountry ?: other.nativeCountry),
                            "socialStatus" to (this.socialStatus ?: other.socialStatus),
                            "mainSourceOfIncome" to (this.mainSourceOfIncome ?: other.mainSourceOfIncome),
                            "schoolingInfos" to mergeListsDistinct(schoolingInfos, other.schoolingInfos),
                            "employementInfos" to mergeListsDistinct(employementInfos, other.employementInfos),
                            "properties" to (other.properties + this.properties),
                            "insurabilities" to mergeListsDistinct(insurabilities, other.insurabilities,
                                    { a, b -> a.insuranceId == b.insuranceId && a.startDate == b.startDate },
                                    { a, b -> if (a.endDate != null) a else b }
                            ),
                            "patientHealthCareParties" to mergeListsDistinct(patientHealthCareParties, other.patientHealthCareParties,
                                    { a, b -> a.healthcarePartyId == b.healthcarePartyId && a.type == b.type },
                                    { a, b -> a.merge(b) }
                            )
    )
    override fun withIdRev(id: String?, rev: String): Patient =
            if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
}

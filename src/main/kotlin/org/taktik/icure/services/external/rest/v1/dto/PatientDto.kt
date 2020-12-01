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
package org.taktik.icure.services.external.rest.v1.dto


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.CryptoActorDto
import org.taktik.icure.services.external.rest.v1.dto.base.EncryptableDto
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.base.PersonDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DeactivationReasonDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.EmploymentInfoDto
import org.taktik.icure.services.external.rest.v1.dto.embed.FinancialInstitutionInformationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.GenderDto
import org.taktik.icure.services.external.rest.v1.dto.embed.InsurabilityDto
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicalHouseContractDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PersonalStatusDto
import org.taktik.icure.services.external.rest.v1.dto.embed.SchoolingInfoDto
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.ValidCode
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PatientDto(
        override val id: String,
        override val rev: String? = null,
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        override val tags: Set<CodeStubDto> = setOf(),
        override val codes: Set<CodeStubDto> = setOf(),
        override val endOfLife: Long? = null,
        override val deletionDate: Long? = null,

        override val firstName: String? = null,
        override val lastName: String? = null, //Is usually either maidenName or spouseName,
        override val companyName: String? = null,
        override val languages: List<String> = listOf(), //alpha-2 code http://www.loc.gov/standards/iso639-2/ascii_8bits.html,
        override val addresses: List<AddressDto> = listOf(),
        override val civility: String? = null,
        override val gender: GenderDto? = GenderDto.unknown,

        val mergeToPatientId: String? = null,
        val mergedIds: Set<String> = HashSet(),
        val nonDuplicateIds: Set<String> = HashSet(),
        val encryptedAdministrativesDocuments: Set<String> = HashSet(),
        val alias: String? = null,
        val active: Boolean = true,
        val deactivationReason: DeactivationReasonDto = DeactivationReasonDto.none,
        val ssin: String? = null,
        val maidenName: String? = null,// Never changes (nom de jeune fille),
        val spouseName: String? = null, // Name of the spouse after marriage,
        val partnerName: String? = null, // Name of the partner, sometimes equal to spouseName,
        val personalStatus: PersonalStatusDto? = PersonalStatusDto.unknown,
        val dateOfBirth: Int? = null, // YYYYMMDD if unknown, 00, ex:20010000 or,
        val dateOfDeath: Int? = null, // YYYYMMDD if unknown, 00, ex:20010000 or,
        val timestampOfLatestEidReading: Long? = null,
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
        val insurabilities: List<InsurabilityDto> = listOf(),
        val partnerships: List<PartnershipDto> = listOf(),
        val patientHealthCareParties: List<PatientHealthCarePartyDto> = listOf(),
        val financialInstitutionInformation: List<FinancialInstitutionInformationDto> = listOf(),
        val medicalHouseContracts: List<MedicalHouseContractDto> = listOf(),
        val parameters: Map<String, List<String>> = mapOf(),

        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE)
        val patientProfessions: List<CodeStubDto> = listOf(),

        val fatherBirthCountry: CodeStubDto? = null,
        val birthCountry: CodeStubDto? = null,
        val nativeCountry: CodeStubDto? = null,
        val socialStatus: CodeStubDto? = null,
        val mainSourceOfIncome: CodeStubDto? = null,
        val schoolingInfos: List<SchoolingInfoDto> = listOf(),
        val employementInfos: List<EmploymentInfoDto> = listOf(),
        val properties: Set<PropertyStubDto> = HashSet(),

        // One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
        // For a pair of HcParties, this key is called the AES exchange key
        // Each HcParty always has one AES exchange key for himself
        // The map's keys are the delegate id.
        // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
        // the key encrypted using delegate's public key.
        override val hcPartyKeys: Map<String, Array<String>> = mapOf(),
        override val privateKeyShamirPartitions: Map<String, String> = mapOf(),
        override val publicKey: String? = null,

        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<DelegationDto>> = mapOf(),
        override val delegations: Map<String, Set<DelegationDto>> = mapOf(),
        override val encryptionKeys: Map<String, Set<DelegationDto>> = mapOf(),
        override val encryptedSelf: String? = null
) : StoredDocumentDto, ICureDocumentDto<String>, PersonDto, EncryptableDto, CryptoActorDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

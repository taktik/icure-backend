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
import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
import org.taktik.icure.services.external.rest.v1.dto.base.PersonDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.FinancialInstitutionInformationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.FlatRateTarificationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.GenderDto
import org.taktik.icure.services.external.rest.v1.dto.embed.HealthcarePartyStatusDto
import org.taktik.icure.services.external.rest.v1.dto.embed.TelecomTypeDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthcarePartyDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        override val name: String? = null,
        override val lastName: String? = null,
        override val firstName: String? = null,
        override val gender: GenderDto? = null,
        override val civility: String? = null,
        override val companyName: String? = null,
        val speciality: String? = null,
        val bankAccount: String? = null,
        val bic: String? = null,
        val proxyBankAccount: String? = null,
        val proxyBic: String? = null,
        val invoiceHeader: String? = null,
        val cbe: String? = null,
        val ehp: String? = null,
        val userId: String? = null,
        val parentId: String? = null,
        val convention: Int? = null, //0,1,2,9
        val nihii: String? = null, //institution, person
        val nihiiSpecCode: String? = null, //don't show field in the GUI
        val ssin: String? = null,
        override val addresses: List<AddressDto> = listOf(),
        override val languages: List<String> = listOf(),
        val picture: ByteArray? = null,
        val statuses: Set<HealthcarePartyStatusDto> = setOf(),

        val specialityCodes: Set<CodeStubDto> = setOf(), //Speciality codes, default is first

        val sendFormats: Map<TelecomTypeDto, String> = mapOf(),
        val notes: String? = null,
        val financialInstitutionInformation: List<FinancialInstitutionInformationDto> = listOf(),

        // Medical houses
        var billingType: String? = null, // "serviceFee" (à l'acte) or "flatRate" (forfait)
        var type: String? = null, // "persphysician" or "medicalHouse" or "perstechnician"
        var contactPerson: String? = null,
        var contactPersonHcpId: String? = null,
        var flatRateTarifications: List<FlatRateTarificationDto> = listOf(),
        var importedData: Map<String, String> = mapOf(),

        val options: Map<String, String> = mapOf(),

        //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
        //For a pair of HcParties, this key is called the AES exchange key
        //Each HcParty always has one AES exchange key for himself
        // The map's keys are the delegate id.
        // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
        // the key encrypted using delegate's public key.
        override val hcPartyKeys: Map<String, Array<String>> = mapOf(),
        override val privateKeyShamirPartitions: Map<String, String> = mapOf(), //Format is hcpId of key that has been partitionned : "threshold|partition in hex"
        override val publicKey: String? = null
) : StoredDocumentDto, NamedDto, PersonDto, CryptoActorDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

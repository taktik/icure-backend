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
package org.taktik.icure.services.external.rest.v2.dto


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.*
import org.taktik.icure.services.external.rest.v2.dto.embed.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity is a root level object. It represents a healthcare party. It is serialized in JSON and saved in the underlying icure-healthcareParty CouchDB database.""")
data class HealthcarePartyDto(
        @Schema(description = "the Id of the healthcare party. We encourage using either a v4 UUID or a HL7 Id.") override val id: String,
        @Schema(description = "the revision of the healthcare party in the database, used for conflict management / optimistic locking.") override val rev: String? = null,
        @Schema(description = "hard delete (unix epoch in ms) timestamp of the object.") override val deletionDate: Long? = null,

        @Schema(description = "The full name of the healthcare party, used mainly when the healthcare party is an organization") override val name: String? = null,
        @Schema(description = "the lastname (surname) of the healthcare party. This is the official lastname that should be used for official administrative purposes.") override val lastName: String? = null,
        @Schema(description = "the firstname (name) of the healthcare party.") override val firstName: String? = null,
        @Schema(description = "the gender of the healthcare party: male, female, indeterminate, changed, changedToMale, changedToFemale, unknown") override val gender: GenderDto? = null,
        @Schema(description = "Mr., Ms., Pr., Dr. ...") override val civility: String? = null,
        @Schema(description = "The name of the company this healthcare party is member of") override val companyName: String? = null,
        @Schema(description = "Medical specialty of the healthcare party") val speciality: String? = null,
        @Schema(description = "Bank Account identifier of the healhtcare party, IBAN, deprecated, use financial institutions instead") val bankAccount: String? = null,
        @Schema(description = "Bank Identifier Code, the SWIFT Address assigned to the bank, use financial institutions instead") val bic: String? = null,
        val proxyBankAccount: String? = null,
        val proxyBic: String? = null,
        @Schema(description = "All details included in the invoice header") val invoiceHeader: String? = null,
        @Schema(description = "Identifier number for institution type if the healthcare party is an enterprise") val cbe: String? = null,
        @Schema(description = "Identifier number for the institution if the healthcare party is an organization") val ehp: String? = null,
        @Schema(description = "The id of the user that usually handles this healthcare party.") val userId: String? = null,
        @Schema(description = "Id of parent of the user representing the healthcare party.") val parentId: String? = null,
        val convention: Int? = null, //0,1,2,9
        @Schema(description = "National Institute for Health and Invalidity Insurance number assigned to healthcare parties (institution or person).") val nihii: String? = null, //institution, person
        val nihiiSpecCode: String? = null, //don't show field in the GUI
        @Schema(description = "Social security inscription number.") val ssin: String? = null,
        @Schema(description = "The list of addresses (with address type).") override val addresses: List<AddressDto> = emptyList(),
        @Schema(description = "The list of languages spoken by the patient ordered by fluency (alpha-2 code http://www.loc.gov/standards/iso639-2/ascii_8bits.html).") override val languages: List<String> = emptyList(),
        @Schema(description = "A picture usually saved in JPEG format.") val picture: ByteArray? = null,
        @Schema(description = "The healthcare party's status: 'trainee' or 'withconvention' or 'accredited'") val statuses: Set<HealthcarePartyStatusDto> = emptySet(),
        @Schema(description = "The healthcare party's status history") val statusHistory: List<HealthcarePartyHistoryStatusDto> = emptyList(),

        @Schema(description = "Medical specialty of the healthcare party codified using FHIR or Kmehr codificaiton scheme") val specialityCodes: Set<CodeStubDto> = emptySet(), //Speciality codes, default is first

        @Schema(description = "The type of format for contacting the healthcare party, ex: mobile, phone, email, etc.") val sendFormats: Map<TelecomTypeDto, String> = emptyMap(),
        @Schema(description = "Text notes.") val notes: String? = null,
        @Schema(description = "List of financial information (Bank, bank account).") val financialInstitutionInformation: List<FinancialInstitutionInformationDto> = emptyList(),

        // Medical houses
        @Schema(description = "The invoicing scheme this healthcare party adheres to : 'service fee' or 'flat rate'") var billingType: String? = null, // "serviceFee" (à l'acte) or "flatRate" (forfait)
        var type: String? = null, // "persphysician" or "medicalHouse" or "perstechnician"
        var contactPerson: String? = null,
        var contactPersonHcpId: String? = null,
        var flatRateTarifications: List<FlatRateTarificationDto> = emptyList(),
        var importedData: Map<String, String> = emptyMap(),

        val options: Map<String, String> = emptyMap(),

        //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
        //For a pair of HcParties, this key is called the AES exchange key
        //Each HcParty always has one AES exchange key for himself
        // The map's keys are the delegate id.
        // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
        // the key encrypted using delegate's public key.
        override val hcPartyKeys: Map<String, Array<String>> = emptyMap(),
        override val privateKeyShamirPartitions: Map<String, String> = emptyMap(), //Format is hcpId of key that has been partitionned : "threshold⎮partition in hex"
        override val publicKey: String? = null
) : StoredDocumentDto, NamedDto, PersonDto, CryptoActorDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

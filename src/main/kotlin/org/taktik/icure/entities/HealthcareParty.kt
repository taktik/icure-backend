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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.CryptoActor
import org.taktik.icure.entities.base.Named
import org.taktik.icure.entities.base.Person
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.FinancialInstitutionInformation
import org.taktik.icure.entities.embed.FlatRateTarification
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.HealthcarePartyStatus
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthcareParty(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        override val name: String? = null,
        override val lastName: String? = null,
        override val firstName: String? = null,
        override val gender: Gender? = null,
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
        override val addresses: List<Address> = listOf(),
        override val languages: List<String> = listOf(),
        val picture: ByteArray? = null,
        val statuses: Set<HealthcarePartyStatus> = setOf(),

        @field:ValidCode(autoFix = AutoFix.NORMALIZECODE) val specialityCodes: Set<CodeStub> = setOf(), //Speciality codes, default is first

        val sendFormats: Map<TelecomType, String> = mapOf(),
        val notes: String? = null,
        val financialInstitutionInformation: List<FinancialInstitutionInformation> = listOf(),

        // Medical houses
        var billingType: String? = null, // "serviceFee" (à l'acte) or "flatRate" (forfait)
        var type: String? = null, // "persphysician" or "medicalHouse" or "perstechnician"
        var contactPerson: String? = null,
        var contactPersonHcpId: String? = null,
        var flatRateTarifications: List<FlatRateTarification> = listOf(),
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
        override val publicKey: String? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null

) : StoredDocument, Named, Person, CryptoActor {
    companion object : DynamicInitializer<HealthcareParty>

    fun merge(other: HealthcareParty) = HealthcareParty(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: HealthcareParty) = super<StoredDocument>.solveConflictsWith(other) + super<Person>.solveConflictsWith(other) + super<CryptoActor>.solveConflictsWith(other) + mapOf(
            "speciality" to (this.speciality ?: other.speciality),
            "bankAccount" to (this.bankAccount ?: other.bankAccount),
            "bic" to (this.bic ?: other.bic),
            "proxyBankAccount" to (this.proxyBankAccount ?: other.proxyBankAccount),
            "proxyBic" to (this.proxyBic ?: other.proxyBic),
            "invoiceHeader" to (this.invoiceHeader ?: other.invoiceHeader),
            "cbe" to (this.cbe ?: other.cbe),
            "ehp" to (this.ehp ?: other.ehp),
            "userId" to (this.userId ?: other.userId),
            "parentId" to (this.parentId ?: other.parentId),
            "convention" to (this.convention ?: other.convention),
            "nihii" to (this.nihii ?: other.nihii),
            "nihiiSpecCode" to (this.nihiiSpecCode ?: other.nihiiSpecCode),
            "ssin" to (this.ssin ?: other.ssin),
            "picture" to (this.picture ?: other.picture),
            "statuses" to (other.statuses + this.statuses),
            "specialityCodes" to (other.specialityCodes + this.specialityCodes),
            "sendFormats" to (other.sendFormats + this.sendFormats),
            "notes" to (this.notes ?: other.notes),
            "financialInstitutionInformation" to mergeListsDistinct(this.financialInstitutionInformation, other.financialInstitutionInformation,
                    { a, b -> a.key?.equals(b.key) ?: false }
            ),
            "billingType" to (this.billingType ?: other.billingType),
            "type" to (this.type ?: other.type),
            "contactPerson" to (this.contactPerson ?: other.contactPerson),
            "contactPersonHcpId" to (this.contactPersonHcpId ?: other.contactPersonHcpId),
            "flatRateTarifications" to mergeListsDistinct(this.flatRateTarifications, other.flatRateTarifications,
                    { a, b -> a.flatRateType?.equals(b.flatRateType) ?: false }
            ),
            "importedData" to (other.importedData + this.importedData),
            "options" to (other.options + this.options)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

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

package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

//NOTE: better classname would be MedicalHouseInscriptionPeriod
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicalHouseContract(
        val contractId: String? = null,
        val validFrom: Long? = null,  //yyyyMMdd : start of contract period
        val validTo: Long? = null,  //yyyyMMdd : end of contract period
        val mmNihii: String? = null,
        val hcpId: String? = null,
        val changeType: ContractChangeType? = null,  //inscription, coverageChange, suspension
        val parentContractId: String? = null,
        val changedBy: String? = null,  //user, mcn

        //Coverage specific data (coverage = forfait-inscription)
        val startOfContract: Long? = null,  //yyyyMMdd : signdate
        val startOfCoverage: Long? = null,  //yyyyMMdd
        val endOfContract: Long? = null,  //yyyyMMdd : signdate
        val endOfCoverage: Long? = null,  //yyyyMMdd
        val kine: Boolean = false,
        val gp: Boolean = false,
        val ptd: Boolean = false,
        val nurse: Boolean = false,
        val noKine: Boolean = false,
        val noGp: Boolean = false,
        val noNurse: Boolean = false,
        val unsubscriptionReasonId: Int? = null,

        val ptdStart: Long? = null,
        val ptdEnd: Long? = null,
        val ptdLastInvoiced: Long? = null,

        //Suspension specific data:
        val startOfSuspension: Long? = null, //yyyyMMdd
        val endOfSuspension: Long? = null, //yyyyMMdd
        val suspensionReason: SuspensionReason? = null,
        val suspensionSource: String? = null,
        val forcedSuspension: Boolean = false, //no automatic unSuspension = false


        val signatureType: MhcSignatureType? = null,
        val status: Int? = null,
        val options: Map<String, String> = HashMap(),
        val receipts: Map<String,String> = mapOf(),

        override val encryptedSelf: String? = null
) : Encrypted

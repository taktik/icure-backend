package org.taktik.icure.services.external.rest.v1.dto.embed

//NOTE: better classname would be MedicalHouseInscriptionPeriod
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicalHouseContractDto(
        val contractId: String? = null,
        val validFrom: Long? = null,  //yyyyMMdd : start of contract period
        val validTo: Long? = null,  //yyyyMMdd : end of contract period
        val mmNihii: String? = null,
        val hcpId: String? = null,
        val changeType: ContractChangeTypeDto? = null,  //inscription, coverageChange, suspension
        val parentContractId: String? = null,
        val changedBy: String? = null,  //user, mcn

        //Coverage specific data (coverage = forfait-inscription)
        val startOfContract: Long? = null,  //yyyyMMdd : signdate
        val startOfCoverage: Long? = null,  //yyyyMMdd
        val endOfContract: Long? = null,  //yyyyMMdd : signdate
        val endOfCoverage: Long? = null,  //yyyyMMdd
        val kine: Boolean = false,
        val gp: Boolean = false,
        val nurse: Boolean = false,
        val noKine: Boolean = false,
        val noGp: Boolean = false,
        val noNurse: Boolean = false,
        val unsubscriptionReasonId: Int? = null,

        //SuspensionDto specific data:
        val startOfSuspension: Long? = null, //yyyyMMdd
        val endOfSuspension: Long? = null, //yyyyMMdd
        val suspensionReason: SuspensionReasonDto? = null,
        val suspensionSource: String? = null,
        val forcedSuspension: Boolean = false, //no automatic unSuspension = false
        val signatureType: MhcSignatureTypeDto? = null,
        val status: Int? = null,
        val receipts: Map<String,String> = mapOf(),

        override val encryptedSelf: String? = null
) : EncryptedDto

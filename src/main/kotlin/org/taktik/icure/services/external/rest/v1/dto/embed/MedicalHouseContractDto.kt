package org.taktik.icure.services.external.rest.v1.dto.embed

//NOTE: better classname would be MedicalHouseInscriptionPeriod
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
        val isKine: Boolean = false,
        val isGp: Boolean = false,
        val isNurse: Boolean = false,
        val isNoKine: Boolean = false,
        val isNoGp: Boolean = false,
        val isNoNurse: Boolean = false,
        val unsubscriptionReasonId: Int? = null,

        //SuspensionDto specific data:
        val startOfSuspension: Long? = null, //yyyyMMdd
        val endOfSuspension: Long? = null, //yyyyMMdd
        val suspensionReason: SuspensionReasonDto? = null,
        val suspensionSource: String? = null,
        val isForcedSuspension: Boolean = false, //no automatic unSuspension = false
        override val encryptedSelf: String? = null
) : EncryptedDto

package org.taktik.icure.entities.embed

//NOTE: better classname would be MedicalHouseInscriptionPeriod
class MedicalHouseContract {
    var contractId: String? = null
        set(contractId) {
            var contractId = contractId
            contractId = contractId
        }
    var validFrom //yyyyMMdd : start of contract period
            : Long? = null
    var validTo //yyyyMMdd : end of contract period
            : Long? = null
    var mmNihii: String? = null
    var hcpId: String? = null
    var changeType //inscription, coverageChange, suspension
            : ContractChangeType? = null
    var parentContractId: String? = null
    var changedBy //user, mcn
            : String? = null

    //Coverage specific data (coverage = forfait-inscription)
    var startOfContract //yyyyMMdd : signdate
            : Long? = null
    var startOfCoverage //yyyyMMdd
            : Long? = null
    var endOfContract //yyyyMMdd : signdate
            : Long? = null
    var endOfCoverage //yyyyMMdd
            : Long? = null
    var isKine = false
    var isGp = false
    var isNurse = false
    var isNoKine = false
    var isNoGp = false
    var isNoNurse = false
    var unsubscriptionReasonId: Int? = null

    //Suspension specific data:
    var startOfSuspension //yyyyMMdd
            : Long? = null
    var endOfSuspension //yyyyMMdd
            : Long? = null
    var suspensionReason: SuspensionReason? = null
    var suspensionSource: String? = null
    var isForcedSuspension //no automatic unSuspension = false

    fun mergeFrom(other: MedicalHouseContract?) {
        //TODO: implement
    }
}

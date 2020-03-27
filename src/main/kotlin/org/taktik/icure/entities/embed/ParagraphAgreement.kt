package org.taktik.icure.entities.embed

import java.io.Serializable

class ParagraphAgreement : Serializable {
    var timestamp: Long? = null
    var paragraph: String? = null
    var accepted: Boolean? = null
    var inTreatment: Boolean? = null
    var canceled: Boolean? = null
    var careProviderReference: String? = null
    var decisionReference: String? = null
    var start: Long? = null
    var end: Long? = null
    var cancelationDate: Long? = null
    var quantityValue: Double? = null
    var quantityUnit: String? = null
    var ioRequestReference: String? = null
    var responseType: String? = null
    var refusalJustification: Map<String, String>? = null
    var verses: Set<Long>? = null
    var coverageType: String? = null
    var unitNumber: Double? = null
    var strength: Double? = null
    var strengthUnit: String? = null
    var agreementAppendices: List<AgreementAppendix>? = null
    var documentId: String? = null

}

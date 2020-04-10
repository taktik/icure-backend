package org.taktik.icure.entities.embed

class IdentityDocumentReader {
    var justificatifDocumentNumber: String? = null
    var supportSerialNumber: String? = null
    var timeReadingEIdDocument: Long? = null
    private var eIdDocumentSupportType = 0
    var reasonManualEncoding = 0
    var reasonUsingVignette = 0

    fun geteIdDocumentSupportType(): Int {
        return eIdDocumentSupportType
    }

    fun seteIdDocumentSupportType(eIdDocumentSupportType: Int) {
        this.eIdDocumentSupportType = eIdDocumentSupportType
    }

}

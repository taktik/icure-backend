package org.taktik.icure.services.external.rest.v1.dto.be.efact

class EfactMessage {
    var detail: String? = null
    var id: String? = null
    var name: String? = null

    var commonOutput: CommonOutput? = null

    var message: List<Record>? = null
    var xades : String? = null
    var tAck: TAck? = null

    var hashValue: String? = null
}

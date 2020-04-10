package org.taktik.icure.entities

import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.RevisionInfo

class MedicalLocation(id: String,
                      rev: String? = null,
                      revisionsInfo: Array<RevisionInfo> = arrayOf(),
                      conflicts: Array<String> = arrayOf(),
                      revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory) {
    var name: String? = null
    var description: String? = null
    var responsible: String? = null
    var guardPost: Boolean? = null
    var cbe: String? = null
    var bic: String? = null
    var bankAccount: String? = null
    var nihii: String? = null
    var ssin: String? = null
    var address: Address? = null
    var agendaIds: List<String>? = null

}

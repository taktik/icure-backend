package org.taktik.icure.entities

import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import java.util.HashMap

class ApplicationSettings(id: String,
                          rev: String? = null,
                          revisionsInfo: Array<RevisionInfo> = arrayOf(),
                          conflicts: Array<String> = arrayOf(),
                          revHistory: Map<String, String> = mapOf()) : StoredDocument(id, rev, revisionsInfo, conflicts, revHistory) {
    var settings: Map<String, String> = HashMap()

}

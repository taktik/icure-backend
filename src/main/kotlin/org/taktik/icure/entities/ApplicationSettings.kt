package org.taktik.icure.entities

import org.taktik.icure.entities.base.StoredDocument
import java.util.HashMap

class ApplicationSettings : StoredDocument() {
    var settings: Map<String, String> = HashMap()

}

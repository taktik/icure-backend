package org.taktik.icure.entities.base

interface CodeIdentification {
    val id: String
    var code: String?
    var type: String?
    var version: String?
}

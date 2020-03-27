package org.taktik.icure.entities.base

interface CodeIdentification {
    var id: String?
    fun getCode(): String?
    fun setCode(code: String)
    fun getType(): String?
    fun setType(type: String)
    fun getVersion(): String?
    fun setVersion(version: String)
}

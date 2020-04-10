package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import java.io.Serializable
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class CodeStub(@property:Json(name = "_id") override val id: String) : Serializable, CodeIdentification {
    override var code: String? = null
    override var type: String? = null
    override var version: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val codeStub = o as CodeStub
        return id == codeStub.id &&
                code == codeStub.code &&
                type == codeStub.type &&
                version == codeStub.version
    }

    override fun hashCode(): Int {
        return Objects.hash(id, code, type, version)
    }
}

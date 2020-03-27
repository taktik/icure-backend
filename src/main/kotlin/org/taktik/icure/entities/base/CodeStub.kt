package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import java.io.Serializable
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class CodeStub : Serializable, CodeIdentification {
    @JsonProperty("_id")
    @Json(name = "_id")
    override var id: String? = null
    var code: String? = null
    var type: String? = null
    var version: String? = null

    constructor() {}
    constructor(type: String, code: String, version: String) {
        this.code = code
        this.type = type
        this.version = version
        id = "$type|$code|$version"
    }

    override fun getCode(): String? {
        return code
    }

    override fun setCode(code: String) {
        this.code = code
    }

    override fun getType(): String? {
        return type
    }

    override fun setType(type: String) {
        this.type = type
    }

    override fun getVersion(): String? {
        return version
    }

    override fun setVersion(version: String) {
        this.version = version
    }

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

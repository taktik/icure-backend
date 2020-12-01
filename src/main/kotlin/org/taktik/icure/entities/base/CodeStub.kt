package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.handlers.CodeStubDeserializer
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = CodeStubDeserializer::class)
@KotlinBuilder
data class CodeStub(
        @JsonProperty("_id") override val id: String,         // id = type|code|version  => this must be unique
        override val context: String? = null, //ex: When embedded the context where this code is used
        override val type: String? = null, //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
        override val code: String? = null, //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
        override val version: String? = null, //ex: 10. Must be lexicographically searchable
        override val label: Map<String, String> = mapOf() //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
) : CodeIdentification, Serializable {
    companion object : DynamicInitializer<CodeStub> {
        fun from(type: String, code: String, version: String) = CodeStub(id = "$type:$code:$version", type = type, code = code, version = version)
    }

    fun merge(other: CodeStub) = CodeStub(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: CodeStub) = super.solveConflictsWith(other)

    override fun normalizeIdentification(): CodeStub {
        val parts = this.id.split("|").toTypedArray()
        return if (this.type == null || this.code == null || this.version == null) this.copy(
                type = this.type ?: parts[0],
                code = this.code ?: parts[1],
                version = this.version ?: parts[2]
        ) else this
    }

}

package org.taktik.icure.entities.samv2.stub

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.samv2.embed.SamText

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VmpGroupStub(
        override val id: String,
        val code: String? = null,
        val name: SamText? = null
) : Identifiable<String>

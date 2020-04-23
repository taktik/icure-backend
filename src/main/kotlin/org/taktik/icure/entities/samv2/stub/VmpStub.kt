package org.taktik.icure.entities.samv2.stub

import com.fasterxml.jackson.annotation.JsonProperty
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.samv2.embed.SamText

data class VmpStub(
        @JsonProperty("_id") override val id: String,
        val code: String? = null,
        val vmpGroup: VmpGroupStub? = null,
        val name: SamText? = null
) : Identifiable<String>

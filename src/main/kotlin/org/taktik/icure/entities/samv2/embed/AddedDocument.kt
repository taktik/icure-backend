package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.samv2.embed.DataPeriod

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AddedDocument(
        override val from: Long?,
        override val to: Long?,
        val verseSeq: Long,
        val documentSeq: Long,
        val mimeType: String? = null,
        val addressUrl: String? = null,
        val descrNl: String? = null,
        val descrFr: String? = null,
        val localPath: String? = null,
) : DataPeriod

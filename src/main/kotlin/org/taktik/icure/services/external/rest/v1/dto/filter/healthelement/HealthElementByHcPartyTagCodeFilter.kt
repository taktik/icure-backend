package org.taktik.icure.services.external.rest.v1.dto.filter.healthelement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v1.dto.filter.AbstractFilterDto

@JsonPolymorphismRoot(AbstractFilterDto::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthElementByHcPartyTagCodeFilter(
        override val desc:String? = null,
        override val healthCarePartyId: String? = null,
        override val codeType: String? = null,
        override val codeNumber: String? = null,
        override val tagType: String? = null,
        override val tagCode: String? = null,
        override val status: Int? = null
        ) : AbstractFilterDto<HealthElement>, org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyTagCodeFilter

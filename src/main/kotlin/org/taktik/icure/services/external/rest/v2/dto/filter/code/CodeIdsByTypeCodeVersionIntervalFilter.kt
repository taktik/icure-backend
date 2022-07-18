package org.taktik.icure.services.external.rest.v2.dto.filter.code

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.Code
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto

@JsonPolymorphismRoot(AbstractFilterDto::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CodeIdsByTypeCodeVersionIntervalFilter (
	override val desc: String? = null,
	override val startType: String? = null,
	override val startCode: String? = null,
	override val startVersion: String? = null,
	override val endType: String? = null,
	override val endCode: String? = null,
	override val endVersion: String? = null
) : AbstractFilterDto<Code>, org.taktik.icure.domain.filter.code.CodeIdsByTypeCodeVersionIntervalFilter

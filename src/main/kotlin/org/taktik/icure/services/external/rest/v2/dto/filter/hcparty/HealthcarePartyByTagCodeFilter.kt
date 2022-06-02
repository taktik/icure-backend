package org.taktik.icure.services.external.rest.v2.dto.filter.hcparty

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.hcparty.HealthcarePartyByTagCodeFilter
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto

@JsonPolymorphismRoot(AbstractFilterDto::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthcarePartyByTagCodeFilter(
	override val tagType: String? = null,
	override val tagCode: String? = null,
	override val codeType: String? = null,
	override val codeCode: String? = null,
	override val desc: String? = null
) : AbstractFilterDto<HealthcareParty>, HealthcarePartyByTagCodeFilter

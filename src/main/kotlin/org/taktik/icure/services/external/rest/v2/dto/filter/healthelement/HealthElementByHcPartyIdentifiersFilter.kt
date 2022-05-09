package org.taktik.icure.services.external.rest.v2.dto.filter.healthelement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto

@JsonPolymorphismRoot(AbstractFilterDto::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthElementByHcPartyIdentifiersFilter(
	override val desc: String? = null,
	override val hcPartyId: String? = null,
	override val identifiers: List<Identifier> = emptyList(),
) : AbstractFilterDto<HealthElement>, org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyIdentifiersFilter

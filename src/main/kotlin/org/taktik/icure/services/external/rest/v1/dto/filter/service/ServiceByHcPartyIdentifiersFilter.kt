package org.taktik.icure.services.external.rest.v1.dto.filter.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifiersFilter
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v1.dto.filter.AbstractFilterDto

@JsonPolymorphismRoot(AbstractFilterDto::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ServiceByHcPartyIdentifiersFilter(
	override val healthcarePartyId: String? = null,
	override val desc: String? = null,
	override val identifiers: List<Identifier> = emptyList()
) : AbstractFilterDto<Service>, ServiceByHcPartyIdentifiersFilter

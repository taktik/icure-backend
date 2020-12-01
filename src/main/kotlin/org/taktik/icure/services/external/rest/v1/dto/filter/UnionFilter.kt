package org.taktik.icure.services.external.rest.v1.dto.filter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.Filters
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.handlers.JsonPolymorphismRoot

@JsonPolymorphismRoot(AbstractFilterDto::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class UnionFilter<O : Identifiable<String>>(
        override val desc: String? = null,
        override val filters: List<AbstractFilterDto<O>> = listOf()
) : AbstractFilterDto<O>, Filters.UnionFilter<String, O>

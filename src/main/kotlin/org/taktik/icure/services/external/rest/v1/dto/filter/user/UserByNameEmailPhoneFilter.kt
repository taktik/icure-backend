package org.taktik.icure.services.external.rest.v1.dto.filter.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.User
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v1.dto.filter.AbstractFilterDto

@JsonPolymorphismRoot(AbstractFilterDto::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class UserByNameEmailPhoneFilter(
	override val searchString: String,
	override val desc: String? = null
) : AbstractFilterDto<User>, org.taktik.icure.domain.filter.user.UserByNameEmailPhoneFilter

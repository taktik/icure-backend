package org.taktik.icure.services.external.rest.v2.dto.security

import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v2.dto.filter.predicate.AlwaysPredicate

@KotlinBuilder
data class AlwaysPermissionItemDto(override val type: PermissionTypeDto) : PermissionItemDto {
	override val predicate = AlwaysPredicate()
}

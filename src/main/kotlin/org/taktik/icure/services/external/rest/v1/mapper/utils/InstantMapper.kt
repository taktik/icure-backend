/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.mapper.utils

import java.time.Instant
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class InstantMapper {
	fun map(instant: Instant): Long {
		return instant.toEpochMilli()
	}

	fun map(aLong: Long?): Instant {
		return Instant.ofEpochMilli(aLong!!)
	}
}

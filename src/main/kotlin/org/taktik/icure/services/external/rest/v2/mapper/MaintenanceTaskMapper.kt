/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v2.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.services.external.rest.v2.dto.MaintenanceTaskDto
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.base.IdentifierV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.base.PropertyStubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper

@Mapper(componentModel = "spring", uses = [CodeStubV2Mapper::class, DelegationV2Mapper::class, PropertyStubV2Mapper::class, IdentifierV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MaintenanceTaskV2Mapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(maintenanceTaskDto: MaintenanceTaskDto): MaintenanceTask
	fun map(maintenanceTask: MaintenanceTask): MaintenanceTaskDto
}

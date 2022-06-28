/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.services.external.rest.v1.dto.MaintenanceTaskDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.PropertyStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, DelegationMapper::class, PropertyStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MaintenanceTaskMapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(maintenanceTaskDto: MaintenanceTaskDto): MaintenanceTask
	fun map(maintenanceTask: MaintenanceTask): MaintenanceTaskDto
}

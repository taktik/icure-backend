package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.FrontEndMigration
import org.taktik.icure.services.external.rest.v1.dto.FrontEndMigrationDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.FrontEndMigrationStatusMapper

@Mapper(componentModel = "spring", uses = [FrontEndMigrationStatusMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface FrontEndMigrationMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(frontEndMigrationDto: FrontEndMigrationDto):FrontEndMigration
	fun map(frontEndMigration: FrontEndMigration):FrontEndMigrationDto
}

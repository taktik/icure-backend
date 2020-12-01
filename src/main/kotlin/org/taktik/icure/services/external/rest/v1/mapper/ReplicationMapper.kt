package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Replication
import org.taktik.icure.services.external.rest.v1.dto.ReplicationDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.DatabaseSynchronizationMapper

@Mapper(componentModel = "spring", uses = [DatabaseSynchronizationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ReplicationMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(replicationDto: ReplicationDto):Replication
	fun map(replication: Replication):ReplicationDto
}

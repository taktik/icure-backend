package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Replication
import org.taktik.icure.services.external.rest.v1.dto.ReplicationDto
@Mapper(componentModel = "spring")
interface ReplicationMapper {
	fun map(replicationDto: ReplicationDto):Replication
	fun map(replication: Replication):ReplicationDto
}

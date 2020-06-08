package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Episode
import org.taktik.icure.services.external.rest.v1.dto.embed.EpisodeDto
@Mapper(componentModel = "spring")
interface EpisodeMapper {
	fun map(episodeDto: EpisodeDto):Episode
	fun map(episode: Episode):EpisodeDto
}

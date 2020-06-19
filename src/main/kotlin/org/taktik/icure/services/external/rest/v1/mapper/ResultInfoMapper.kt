package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.dto.result.ResultInfo
import org.taktik.icure.services.external.rest.v1.dto.ResultInfoDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper

@Mapper(componentModel = "spring", uses = [DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ResultInfoMapper {
    fun map(resultInfoDto: ResultInfoDto): ResultInfo
    fun map(resultInfo: ResultInfo): ResultInfoDto
}

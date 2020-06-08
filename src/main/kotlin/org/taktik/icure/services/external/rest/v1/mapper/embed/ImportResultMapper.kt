package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.services.external.rest.v1.dto.ImportResultDto

@Mapper(componentModel = "spring")
interface ImportResultMapper {
    fun map(importResultDto: ImportResultDto): ImportResult
    fun map(importResult: ImportResult): ImportResultDto
}

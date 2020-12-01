package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.services.external.rest.v1.dto.ImportResultDto
import org.taktik.icure.services.external.rest.v1.mapper.ContactMapper
import org.taktik.icure.services.external.rest.v1.mapper.DocumentMapper
import org.taktik.icure.services.external.rest.v1.mapper.FormMapper
import org.taktik.icure.services.external.rest.v1.mapper.HealthElementMapper
import org.taktik.icure.services.external.rest.v1.mapper.HealthcarePartyMapper
import org.taktik.icure.services.external.rest.v1.mapper.PatientMapper

@Mapper(componentModel = "spring", uses = [DelegationMapper::class, PatientMapper::class, HealthElementMapper::class, ContactMapper::class, FormMapper::class, HealthcarePartyMapper::class, DocumentMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ImportResultMapper {
    @Mappings(
            Mapping(target = "warning", ignore = true),
            Mapping(target = "error", ignore = true)
    )
    fun map(importResultDto: ImportResultDto): ImportResult
    fun map(importResult: ImportResult): ImportResultDto
}

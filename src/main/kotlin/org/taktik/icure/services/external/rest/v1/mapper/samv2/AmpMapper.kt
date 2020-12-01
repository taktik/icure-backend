package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.services.external.rest.v1.dto.samv2.AmpDto
import org.taktik.icure.services.external.rest.v1.mapper.EntityReferenceMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.AmpComponentMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.AmpStatusMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.AmppMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.CompanyMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.MedicineTypeMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.SamTextMapper

@Mapper(componentModel = "spring", uses = [CompanyMapper::class, AmpStatusMapper::class, SamTextMapper::class, VmpStubMapper::class, MedicineTypeMapper::class, AmpComponentMapper::class, EntityReferenceMapper::class, AmppMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AmpMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(ampDto: AmpDto):Amp
	fun map(amp: Amp):AmpDto
}

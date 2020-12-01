package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.services.external.rest.v1.dto.samv2.VmpGroupDto
import org.taktik.icure.services.external.rest.v1.mapper.EntityReferenceMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.NoGenericPrescriptionReasonMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.NoSwitchReasonMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.SamTextMapper

@Mapper(componentModel = "spring", uses = [NoSwitchReasonMapper::class, SamTextMapper::class, NoGenericPrescriptionReasonMapper::class, EntityReferenceMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface VmpGroupMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(vmpGroupDto: VmpGroupDto):VmpGroup
    @Mappings(
            Mapping(target = "productId", ignore = true)
    )
	fun map(vmpGroup: VmpGroup):VmpGroupDto
}

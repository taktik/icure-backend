package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Contact
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.ServiceMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.SubContactMapper

@Mapper(componentModel = "spring", uses = [SubContactMapper::class, CodeStubMapper::class, DelegationMapper::class, ServiceMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ContactMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
    fun map(contactDto: ContactDto): Contact
    fun map(contact: Contact): ContactDto
}

/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.samv2.stub

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.stub.SubstanceStub
import org.taktik.icure.services.external.rest.v1.dto.samv2.stub.SubstanceStubDto
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.SamTextMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.StandardSubstanceMapper

@Mapper(componentModel = "spring", uses = [StandardSubstanceMapper::class, SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SubstanceStubMapper {
	fun map(substanceDto: SubstanceStubDto): SubstanceStub
	fun map(substance: SubstanceStub): SubstanceStubDto
}

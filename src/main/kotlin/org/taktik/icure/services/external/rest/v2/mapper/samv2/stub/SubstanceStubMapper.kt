/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v2.mapper.samv2.stub

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.stub.SubstanceStub
import org.taktik.icure.services.external.rest.v2.dto.samv2.stub.SubstanceStubDto
import org.taktik.icure.services.external.rest.v2.mapper.samv2.embed.SamTextV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.samv2.embed.StandardSubstanceV2Mapper

@Mapper(componentModel = "spring", uses = [StandardSubstanceV2Mapper::class, SamTextV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SubstanceStubV2Mapper {
	fun map(substanceDto: SubstanceStubDto): SubstanceStub
	fun map(substance: SubstanceStub): SubstanceStubDto
}

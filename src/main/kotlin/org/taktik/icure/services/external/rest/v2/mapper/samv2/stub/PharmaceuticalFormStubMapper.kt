/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v2.mapper.samv2.stub

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.stub.PharmaceuticalFormStub
import org.taktik.icure.services.external.rest.v2.dto.samv2.stub.PharmaceuticalFormStubDto
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.samv2.embed.SamTextV2Mapper

@Mapper(componentModel = "spring", uses = [SamTextV2Mapper::class, CodeStubV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PharmaceuticalFormStubV2Mapper {
	fun map(pharmaceuticalFormDto: PharmaceuticalFormStubDto): PharmaceuticalFormStub
	fun map(pharmaceuticalForm: PharmaceuticalFormStub): PharmaceuticalFormStubDto
}

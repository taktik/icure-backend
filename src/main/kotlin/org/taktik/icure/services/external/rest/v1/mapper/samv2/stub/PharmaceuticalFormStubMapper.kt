/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.samv2.stub

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.stub.PharmaceuticalFormStub
import org.taktik.icure.services.external.rest.v1.dto.samv2.stub.PharmaceuticalFormStubDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.SamTextMapper

@Mapper(componentModel = "spring", uses = [SamTextMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PharmaceuticalFormStubMapper {
	fun map(pharmaceuticalFormDto: PharmaceuticalFormStubDto): PharmaceuticalFormStub
	fun map(pharmaceuticalForm: PharmaceuticalFormStub): PharmaceuticalFormStubDto
}

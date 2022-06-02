/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Patient
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.IdentifierMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.PropertyStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DeactivationReasonMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.EmploymentInfoMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.FinancialInstitutionInformationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.GenderMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.InsurabilityMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.MedicalHouseContractMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PartnershipMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PatientHealthCarePartyMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PersonNameMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PersonalStatusMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.SchoolingInfoMapper

@Mapper(componentModel = "spring", uses = [IdentifierMapper::class, GenderMapper::class, FinancialInstitutionInformationMapper::class, PersonalStatusMapper::class, SchoolingInfoMapper::class, AddressMapper::class, EmploymentInfoMapper::class, MedicalHouseContractMapper::class, DeactivationReasonMapper::class, PatientHealthCarePartyMapper::class, PropertyStubMapper::class, CodeStubMapper::class, DelegationMapper::class, InsurabilityMapper::class, PartnershipMapper::class, PersonNameMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PatientMapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(patientDto: PatientDto): Patient
	fun map(patient: Patient): PatientDto
}

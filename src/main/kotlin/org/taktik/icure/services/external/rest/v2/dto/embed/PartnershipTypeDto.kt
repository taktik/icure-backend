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
package org.taktik.icure.services.external.rest.v2.dto.embed

import org.taktik.icure.services.external.rest.v2.dto.base.EnumVersionDto

@EnumVersionDto(1L)
enum class PartnershipTypeDto {
	primary_contact, primary_contact_for, family, friend, counselor, contact, //From Kmehr
	brother, brotherinlaw, child, daughter, employer, father, grandchild, grandparent, husband, lawyer, mother, neighbour, notary, partner, sister, sisterinlaw, son, spouse, stepdaughter, stepfather, stepmother, stepson, tutor,
	next_of_kin, federal_agency, insurance_company, state_agency, unknown, //from FHIR : http://terminology.hl7.org/CodeSystem/v2-0131
	seealso, refer //from FHIR : https://www.hl7.org/fhir/codesystem-link-type.html
}

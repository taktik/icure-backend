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
package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.EnumVersionDto

@EnumVersionDto(1L)
enum class PartnershipTypeDto(val fhirCode: String? = null) {
    primary_contact("C"), primary_contact_for, family, friend, counselor, contact,  //From Kmehr
    brother, brotherinlaw, child, daughter, employer("E"), father, grandchild, grandparent, husband, lawyer, mother, neighbour, notary, partner, sister, sisterinlaw, son, spouse, stepdaughter, stepfather, stepmother, stepson, tutor,
    next_of_kin("N"), federal_agency("F"), insurance_company("I"), state_agency("S"), unknown("U") //from FHIR : http://terminology.hl7.org/CodeSystem/v2-0131
}

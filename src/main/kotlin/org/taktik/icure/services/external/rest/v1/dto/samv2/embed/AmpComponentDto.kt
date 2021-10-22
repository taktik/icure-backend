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

package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.samv2.stub.PharmaceuticalFormStubDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AmpComponentDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val ingredients: List<IngredientDto>? = null,
        val pharmaceuticalForms: List<PharmaceuticalFormStubDto>? = null,
        val routeOfAdministrations: List<RouteOfAdministrationDto>? = null,
        val dividable: String? = null,
        val scored: String? = null,
        val crushable: CrushableDto? = null,
        val containsAlcohol: ContainsAlcoholDto? = null,
        val sugarFree: Boolean? = null,
        val modifiedReleaseType: Int? = null,
        val specificDrugDevice: Int? = null,
        val dimensions: String? = null,
        val name: SamTextDto? = null,
        val note: SamTextDto? = null) : DataPeriodDto

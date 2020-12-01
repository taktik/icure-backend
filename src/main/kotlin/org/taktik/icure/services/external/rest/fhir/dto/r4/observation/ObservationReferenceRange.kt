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

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.observation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * Provides guide for interpretation
 *
 * Guidance on how to interpret the value by comparison to a normal or recommended range.  Multiple
 * reference ranges are interpreted as an "OR".   In other words, to represent two distinct target
 * populations, two `referenceRange` elements would be used.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ObservationReferenceRange(
  /**
   * Applicable age range, if relevant
   */
  val age: Range? = null,
  val appliesTo: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * High Range, if relevant
   */
  val high: Quantity? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Low Range, if relevant
   */
  val low: Quantity? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Text based reference range in an observation
   */
  val text: String? = null,
  /**
   * Reference range qualifier
   */
  val type: CodeableConcept? = null
) : BackboneElement

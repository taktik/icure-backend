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
package org.taktik.icure.services.external.rest.fhir.dto.r4.specimendefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The specimen's container
 *
 * The specimen's container.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SpecimenDefinitionTypeTestedContainer(
  val additive: List<SpecimenDefinitionTypeTestedContainerAdditive> = listOf(),
  /**
   * Color of container cap
   */
  val cap: CodeableConcept? = null,
  /**
   * Container capacity
   */
  val capacity: Quantity? = null,
  /**
   * Container description
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Container material
   */
  val material: CodeableConcept? = null,
  /**
   * Minimum volume
   */
  val minimumVolumeQuantity: Quantity? = null,
  /**
   * Minimum volume
   */
  val minimumVolumeString: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Specimen container preparation
   */
  val preparation: String? = null,
  /**
   * Kind of container associated with the kind of specimen
   */
  val type: CodeableConcept? = null
) : BackboneElement

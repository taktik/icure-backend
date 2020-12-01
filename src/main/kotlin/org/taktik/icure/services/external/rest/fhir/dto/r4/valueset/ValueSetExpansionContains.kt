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
package org.taktik.icure.services.external.rest.fhir.dto.r4.valueset

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Codes in the value set
 *
 * The codes that are contained in the value set expansion.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ValueSetExpansionContains(
  /**
   * If user cannot select this entry
   */
  val abstract: Boolean? = null,
  /**
   * Code - if blank, this is not a selectable code
   */
  val code: String? = null,
  val contains: List<ValueSetExpansionContains> = listOf(),
  val designation: List<ValueSetComposeIncludeConceptDesignation> = listOf(),
  /**
   * User display for the concept
   */
  val display: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * If concept is inactive in the code system
   */
  val inactive: Boolean? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * System value for the code
   */
  val system: String? = null,
  /**
   * Version in which this code/display is defined
   */
  val version: String? = null
) : BackboneElement

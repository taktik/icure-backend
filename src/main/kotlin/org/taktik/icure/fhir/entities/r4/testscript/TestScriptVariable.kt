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
package org.taktik.icure.fhir.entities.r4.testscript

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Placeholder for evaluated elements
 *
 * Variable is set based either on element value in response body or on header field value in the
 * response headers.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TestScriptVariable(
  /**
   * Default, hard-coded, or user-defined value for this variable
   */
  val defaultValue: String? = null,
  /**
   * Natural language description of the variable
   */
  val description: String? = null,
  /**
   * The FHIRPath expression against the fixture body
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * HTTP header field name for source
   */
  val headerField: String? = null,
  /**
   * Hint help text for default value to enter
   */
  val hint: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Descriptive name for this variable
   */
  val name: String? = null,
  /**
   * XPath or JSONPath against the fixture body
   */
  val path: String? = null,
  /**
   * Fixture Id of source expression or headerField within this variable
   */
  val sourceId: String? = null
) : BackboneElement

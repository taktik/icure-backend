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
package org.taktik.icure.fhir.entities.r4.immunizationevaluation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Immunization evaluation information
 *
 * Describes a comparison of an immunization event against published recommendations to determine if
 * the administration is "valid" in relation to those  recommendations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImmunizationEvaluation(
  /**
   * Who is responsible for publishing the recommendations
   */
  val authority: Reference? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Date evaluation was performed
   */
  val date: String? = null,
  /**
   * Evaluation notes
   */
  val description: String? = null,
  /**
   * Dose number within series
   */
  val doseNumberPositiveInt: Int? = null,
  /**
   * Dose number within series
   */
  val doseNumberString: String? = null,
  /**
   * Status of the dose relative to published recommendations
   */
  val doseStatus: CodeableConcept,
  val doseStatusReason: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * Immunization being evaluated
   */
  val immunizationEvent: Reference,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Who this evaluation is for
   */
  val patient: Reference,
  /**
   * Name of vaccine series
   */
  val series: String? = null,
  /**
   * Recommended number of doses for immunity
   */
  val seriesDosesPositiveInt: Int? = null,
  /**
   * Recommended number of doses for immunity
   */
  val seriesDosesString: String? = null,
  /**
   * completed | entered-in-error
   */
  val status: String? = null,
  /**
   * Evaluation target disease
   */
  val targetDisease: CodeableConcept,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

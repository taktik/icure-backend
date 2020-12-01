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
package org.taktik.icure.services.external.rest.fhir.dto.r4.immunizationrecommendation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Vaccine administration recommendations
 *
 * Vaccine administration recommendations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImmunizationRecommendationRecommendation(
  val contraindicatedVaccineCode: List<CodeableConcept> = listOf(),
  val dateCriterion: List<ImmunizationRecommendationRecommendationDateCriterion> = listOf(),
  /**
   * Protocol details
   */
  val description: String? = null,
  /**
   * Recommended dose number within series
   */
  val doseNumberPositiveInt: Int? = null,
  /**
   * Recommended dose number within series
   */
  val doseNumberString: String? = null,
  override val extension: List<Extension> = listOf(),
  val forecastReason: List<CodeableConcept> = listOf(),
  /**
   * Vaccine recommendation status
   */
  val forecastStatus: CodeableConcept,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name of vaccination series
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
  val supportingImmunization: List<Reference> = listOf(),
  val supportingPatientInformation: List<Reference> = listOf(),
  /**
   * Disease to be immunized against
   */
  val targetDisease: CodeableConcept? = null,
  val vaccineCode: List<CodeableConcept> = listOf()
) : BackboneElement

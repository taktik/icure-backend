//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.immunizationrecommendation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

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

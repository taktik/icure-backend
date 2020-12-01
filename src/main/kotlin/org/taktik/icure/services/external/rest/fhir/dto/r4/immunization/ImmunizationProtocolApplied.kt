//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.immunization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Protocol followed by the provider
 *
 * The protocol (set of recommendations) being followed by the provider who administered the dose.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImmunizationProtocolApplied(
  /**
   * Who is responsible for publishing the recommendations
   */
  val authority: Reference? = null,
  /**
   * Dose number within series
   */
  val doseNumberPositiveInt: Int? = null,
  /**
   * Dose number within series
   */
  val doseNumberString: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
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
  val targetDisease: List<CodeableConcept> = listOf()
) : BackboneElement

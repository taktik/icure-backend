//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationadministration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.ratio.Ratio

/**
 * Details of how medication was taken
 *
 * Describes the medication dosage information details e.g. dose, rate, site, route, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationAdministrationDosage(
  /**
   * Amount of medication per dose
   */
  val dose: Quantity? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * How drug was administered
   */
  val method: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Dose quantity per unit of time
   */
  val rateQuantity: Quantity? = null,
  /**
   * Dose quantity per unit of time
   */
  val rateRatio: Ratio? = null,
  /**
   * Path of substance into body
   */
  val route: CodeableConcept? = null,
  /**
   * Body site administered to
   */
  val site: CodeableConcept? = null,
  /**
   * Free text dosage instructions e.g. SIG
   */
  val text: String? = null
) : BackboneElement

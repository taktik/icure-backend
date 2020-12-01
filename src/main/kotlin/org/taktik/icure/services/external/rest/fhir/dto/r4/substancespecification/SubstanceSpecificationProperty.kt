//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancespecification

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * General specifications for this substance, including how it is related to other substances
 *
 * General specifications for this substance, including how it is related to other substances.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSpecificationProperty(
  /**
   * Quantitative value for this property
   */
  val amountQuantity: Quantity? = null,
  /**
   * Quantitative value for this property
   */
  val amountString: String? = null,
  /**
   * A category for this property, e.g. Physical, Chemical, Enzymatic
   */
  val category: CodeableConcept? = null,
  /**
   * Property type e.g. viscosity, pH, isoelectric point
   */
  val code: CodeableConcept? = null,
  /**
   * A substance upon which a defining property depends (e.g. for solubility: in water, in alcohol)
   */
  val definingSubstanceCodeableConcept: CodeableConcept? = null,
  /**
   * A substance upon which a defining property depends (e.g. for solubility: in water, in alcohol)
   */
  val definingSubstanceReference: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Parameters that were used in the measurement of a property (e.g. for viscosity: measured at 20C
   * with a pH of 7.1)
   */
  val parameters: String? = null
) : BackboneElement

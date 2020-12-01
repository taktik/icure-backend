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
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier

/**
 * Moiety, for structural modifications
 *
 * Moiety, for structural modifications.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSpecificationMoiety(
  /**
   * Quantitative value for this moiety
   */
  val amountQuantity: Quantity? = null,
  /**
   * Quantitative value for this moiety
   */
  val amountString: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Identifier by which this moiety substance is known
   */
  val identifier: Identifier? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Molecular formula
   */
  val molecularFormula: String? = null,
  /**
   * Textual name for this moiety substance
   */
  val name: String? = null,
  /**
   * Optical activity type
   */
  val opticalActivity: CodeableConcept? = null,
  /**
   * Role that the moiety is playing
   */
  val role: CodeableConcept? = null,
  /**
   * Stereochemistry type
   */
  val stereochemistry: CodeableConcept? = null
) : BackboneElement

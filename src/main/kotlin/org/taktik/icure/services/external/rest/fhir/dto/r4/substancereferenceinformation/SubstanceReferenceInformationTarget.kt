//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancereferenceinformation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Todo
 *
 * Todo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceReferenceInformationTarget(
  /**
   * Todo
   */
  val amountQuantity: Quantity? = null,
  /**
   * Todo
   */
  val amountRange: Range? = null,
  /**
   * Todo
   */
  val amountString: String? = null,
  /**
   * Todo
   */
  val amountType: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Todo
   */
  val interaction: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Todo
   */
  val organism: CodeableConcept? = null,
  /**
   * Todo
   */
  val organismType: CodeableConcept? = null,
  val source: List<Reference> = listOf(),
  /**
   * Todo
   */
  val target: Identifier? = null,
  /**
   * Todo
   */
  val type: CodeableConcept? = null
) : BackboneElement

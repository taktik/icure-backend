//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicinalproduct

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Indicates if the medicinal product has an orphan designation for the treatment of a rare disease
 *
 * Indicates if the medicinal product has an orphan designation for the treatment of a rare disease.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductSpecialDesignation(
  /**
   * Date when the designation was granted
   */
  val date: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * Condition for which the medicinal use applies
   */
  val indicationCodeableConcept: CodeableConcept? = null,
  /**
   * Condition for which the medicinal use applies
   */
  val indicationReference: Reference? = null,
  /**
   * The intended use of the product, e.g. prevention, treatment
   */
  val intendedUse: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Animal species for which this applies
   */
  val species: CodeableConcept? = null,
  /**
   * For example granted, pending, expired or withdrawn
   */
  val status: CodeableConcept? = null,
  /**
   * The type of special designation, e.g. orphan drug, minor use
   */
  val type: CodeableConcept? = null
) : BackboneElement

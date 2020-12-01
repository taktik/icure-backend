//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicinalproductcontraindication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Information about the use of the medicinal product in relation to other therapies described as
 * part of the indication
 *
 * Information about the use of the medicinal product in relation to other therapies described as
 * part of the indication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductContraindicationOtherTherapy(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Reference to a specific medication (active substance, medicinal product or class of products)
   * as part of an indication or contraindication
   */
  val medicationCodeableConcept: CodeableConcept,
  /**
   * Reference to a specific medication (active substance, medicinal product or class of products)
   * as part of an indication or contraindication
   */
  val medicationReference: Reference,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The type of relationship between the medicinal product indication or contraindication and
   * another therapy
   */
  val therapyRelationshipType: CodeableConcept
) : BackboneElement

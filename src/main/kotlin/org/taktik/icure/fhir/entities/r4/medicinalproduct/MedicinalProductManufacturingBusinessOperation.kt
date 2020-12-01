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
 * An operation applied to the product, for manufacturing or adminsitrative purpose
 *
 * An operation applied to the product, for manufacturing or adminsitrative purpose.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductManufacturingBusinessOperation(
  /**
   * Regulatory authorization reference number
   */
  val authorisationReferenceNumber: Identifier? = null,
  /**
   * To indicate if this proces is commercially confidential
   */
  val confidentialityIndicator: CodeableConcept? = null,
  /**
   * Regulatory authorization date
   */
  val effectiveDate: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val manufacturer: List<Reference> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The type of manufacturing operation
   */
  val operationType: CodeableConcept? = null,
  /**
   * A regulator which oversees the operation
   */
  val regulator: Reference? = null
) : BackboneElement

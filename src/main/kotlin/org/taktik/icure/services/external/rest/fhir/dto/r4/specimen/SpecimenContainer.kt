//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.specimen

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Direct container of specimen (tube/slide, etc.)
 *
 * The container holding the specimen.  The recursive nature of containers; i.e. blood in tube in
 * tray in rack is not addressed here.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SpecimenContainer(
  /**
   * Additive associated with container
   */
  val additiveCodeableConcept: CodeableConcept? = null,
  /**
   * Additive associated with container
   */
  val additiveReference: Reference? = null,
  /**
   * Container volume or size
   */
  val capacity: Quantity? = null,
  /**
   * Textual description of the container
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Quantity of specimen within container
   */
  val specimenQuantity: Quantity? = null,
  /**
   * Kind of container directly associated with specimen
   */
  val type: CodeableConcept? = null
) : BackboneElement

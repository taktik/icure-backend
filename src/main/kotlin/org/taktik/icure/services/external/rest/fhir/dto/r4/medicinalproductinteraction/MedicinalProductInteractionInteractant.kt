//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproductinteraction

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * The specific medication, food or laboratory test that interacts
 *
 * The specific medication, food or laboratory test that interacts.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductInteractionInteractant(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The specific medication, food or laboratory test that interacts
   */
  val itemCodeableConcept: CodeableConcept,
  /**
   * The specific medication, food or laboratory test that interacts
   */
  val itemReference: Reference,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

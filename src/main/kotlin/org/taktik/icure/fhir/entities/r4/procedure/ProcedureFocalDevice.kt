//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.procedure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Manipulated, implanted, or removed device
 *
 * A device that is implanted, removed or otherwise manipulated (calibration, battery replacement,
 * fitting a prosthesis, attaching a wound-vac, etc.) as a focal portion of the Procedure.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ProcedureFocalDevice(
  /**
   * Kind of change to device
   */
  val action: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Device that was changed
   */
  val manipulated: Reference,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

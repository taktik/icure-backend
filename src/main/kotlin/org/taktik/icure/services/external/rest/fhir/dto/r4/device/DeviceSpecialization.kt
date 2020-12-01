//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.device

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The capabilities supported on a  device, the standards to which the device conforms for a
 * particular purpose, and used for the communication
 *
 * The capabilities supported on a  device, the standards to which the device conforms for a
 * particular purpose, and used for the communication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DeviceSpecialization(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The standard that is used to operate and communicate
   */
  val systemType: CodeableConcept,
  /**
   * The version of the standard that is used to operate and communicate
   */
  val version: String? = null
) : BackboneElement

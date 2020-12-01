//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.device

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The name of the device as given by the manufacturer
 *
 * This represents the manufacturer's name of the device as provided by the device, from a UDI
 * label, or by a person describing the Device.  This typically would be used when a person provides
 * the name(s) or when the device represents one of the names available from DeviceDefinition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DeviceDeviceName(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The name of the device
   */
  val name: String? = null,
  /**
   * udi-label-name | user-friendly-name | patient-reported-name | manufacturer-name | model-name |
   * other
   */
  val type: String? = null
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.device

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Unique Device Identifier (UDI) Barcode string
 *
 * Unique device identifier (UDI) assigned to device label or package.  Note that the Device may
 * include multiple udiCarriers as it either may include just the udiCarrier for the jurisdiction it is
 * sold, or for multiple jurisdictions it could have been sold.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DeviceUdiCarrier(
  /**
   * UDI Machine Readable Barcode String
   */
  val carrierAIDC: String? = null,
  /**
   * UDI Human Readable Barcode String
   */
  val carrierHRF: String? = null,
  /**
   * Mandatory fixed portion of UDI
   */
  val deviceIdentifier: String? = null,
  /**
   * barcode | rfid | manual +
   */
  val entryType: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * UDI Issuing Organization
   */
  val issuer: String? = null,
  /**
   * Regional UDI authority
   */
  val jurisdiction: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

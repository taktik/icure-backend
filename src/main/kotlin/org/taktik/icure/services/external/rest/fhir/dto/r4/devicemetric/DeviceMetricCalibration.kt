//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.devicemetric

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Describes the calibrations that have been performed or that are required to be performed
 *
 * Describes the calibrations that have been performed or that are required to be performed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DeviceMetricCalibration(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * not-calibrated | calibration-required | calibrated | unspecified
   */
  val state: String? = null,
  /**
   * Describes the time last calibration has been performed
   */
  val time: String? = null,
  /**
   * unspecified | offset | gain | two-point
   */
  val type: String? = null
) : BackboneElement

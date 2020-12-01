//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.sampleddata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * A series of measurements taken by a device
 *
 * A series of measurements taken by a device, with upper and lower limits. There may be more than
 * one dimension in the data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SampledData(
  /**
   * Decimal values with spaces, or "E" | "U" | "L"
   */
  val data: String? = null,
  /**
   * Number of sample points at each time point
   */
  val dimensions: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Multiply data by this before adding to origin
   */
  val factor: Float? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Lower limit of detection
   */
  val lowerLimit: Float? = null,
  /**
   * Zero value and units
   */
  val origin: Quantity,
  /**
   * Number of milliseconds between samples
   */
  val period: Float? = null,
  /**
   * Upper limit of detection
   */
  val upperLimit: Float? = null
) : Element

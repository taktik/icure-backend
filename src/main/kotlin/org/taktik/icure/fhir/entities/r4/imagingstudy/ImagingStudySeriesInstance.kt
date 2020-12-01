//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.imagingstudy

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * A single SOP instance from the series
 *
 * A single SOP instance within the series, e.g. an image, or presentation state.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImagingStudySeriesInstance(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The number of this instance in the series
   */
  val number: Int? = null,
  /**
   * DICOM class type
   */
  val sopClass: Coding,
  /**
   * Description of instance
   */
  val title: String? = null,
  /**
   * DICOM SOP Instance UID
   */
  val uid: String? = null
) : BackboneElement

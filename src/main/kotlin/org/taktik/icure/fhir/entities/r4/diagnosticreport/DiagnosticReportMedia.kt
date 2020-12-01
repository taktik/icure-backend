//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.diagnosticreport

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Key images associated with this report
 *
 * A list of key images associated with this report. The images are generally created during the
 * diagnostic process, and may be directly of the patient, or of treated specimens (i.e. slides of
 * interest).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DiagnosticReportMedia(
  /**
   * Comment about the image (e.g. explanation)
   */
  val comment: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Reference to the image source
   */
  val link: Reference,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

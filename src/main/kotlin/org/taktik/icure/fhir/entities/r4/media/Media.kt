/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.media

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * A photo, video, or audio recording acquired or used in healthcare. The actual content may be
 * inline or provided by direct reference
 *
 * A photo, video, or audio recording acquired or used in healthcare. The actual content may be
 * inline or provided by direct reference.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Media(
  val basedOn: List<Reference> = listOf(),
  /**
   * Observed body part
   */
  val bodySite: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Actual Media - reference or data
   */
  val content: Attachment,
  /**
   * When Media was collected
   */
  val createdDateTime: String? = null,
  /**
   * When Media was collected
   */
  val createdPeriod: Period? = null,
  /**
   * Observing Device
   */
  val device: Reference? = null,
  /**
   * Name of the device/manufacturer
   */
  val deviceName: String? = null,
  /**
   * Length in seconds (audio / video)
   */
  val duration: Float? = null,
  /**
   * Encounter associated with media
   */
  val encounter: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Number of frames if > 1 (photo)
   */
  val frames: Int? = null,
  /**
   * Height of the image in pixels (photo/video)
   */
  val height: Int? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Date/Time this version was made available
   */
  val issued: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  /**
   * The type of acquisition equipment/process
   */
  val modality: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * The person who generated the image
   */
  val operator: Reference? = null,
  val partOf: List<Reference> = listOf(),
  val reasonCode: List<CodeableConcept> = listOf(),
  /**
   * preparation | in-progress | not-done | on-hold | stopped | completed | entered-in-error |
   * unknown
   */
  val status: String? = null,
  /**
   * Who/What this Media is a record of
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Classification of media as image, video, or audio
   */
  val type: CodeableConcept? = null,
  /**
   * Imaging view, e.g. Lateral or Antero-posterior
   */
  val view: CodeableConcept? = null,
  /**
   * Width of the image in pixels (photo/video)
   */
  val width: Int? = null
) : DomainResource

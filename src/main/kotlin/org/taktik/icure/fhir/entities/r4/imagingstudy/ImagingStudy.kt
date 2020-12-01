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
package org.taktik.icure.fhir.entities.r4.imagingstudy

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * A set of images produced in single study (one or more series of references images)
 *
 * Representation of the content produced in a DICOM imaging study. A study comprises a set of
 * series, each of which includes a set of Service-Object Pair Instances (SOP Instances - images or
 * other data) acquired or produced in a common context.  A series is of only one modality (e.g. X-ray,
 * CT, MR, ultrasound), but a study may have multiple series of different modalities.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImagingStudy(
  val basedOn: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Institution-generated description
   */
  val description: String? = null,
  /**
   * Encounter with which this imaging study is associated
   */
  val encounter: Reference? = null,
  val endpoint: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val interpreter: List<Reference> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Where ImagingStudy occurred
   */
  val location: Reference? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  val modality: List<Coding> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * Number of Study Related Instances
   */
  val numberOfInstances: Int? = null,
  /**
   * Number of Study Related Series
   */
  val numberOfSeries: Int? = null,
  val procedureCode: List<CodeableConcept> = listOf(),
  /**
   * The performed Procedure reference
   */
  val procedureReference: Reference? = null,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * Referring physician
   */
  val referrer: Reference? = null,
  val series: List<ImagingStudySeries> = listOf(),
  /**
   * When the study was started
   */
  val started: String? = null,
  /**
   * registered | available | cancelled | entered-in-error | unknown
   */
  val status: String? = null,
  /**
   * Who or what is the subject of the study
   */
  val subject: Reference,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

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
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Each study has one or more series of instances
 *
 * Each study has one or more series of images or other content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImagingStudySeries(
  /**
   * Body part examined
   */
  val bodySite: Coding? = null,
  /**
   * A short human readable summary of the series
   */
  val description: String? = null,
  val endpoint: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val instance: List<ImagingStudySeriesInstance> = listOf(),
  /**
   * Body part laterality
   */
  val laterality: Coding? = null,
  /**
   * The modality of the instances in the series
   */
  val modality: Coding,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Numeric identifier of this series
   */
  val number: Int? = null,
  /**
   * Number of Series Related Instances
   */
  val numberOfInstances: Int? = null,
  val performer: List<ImagingStudySeriesPerformer> = listOf(),
  val specimen: List<Reference> = listOf(),
  /**
   * When the series started
   */
  val started: String? = null,
  /**
   * DICOM Series Instance UID for the series
   */
  val uid: String? = null
) : BackboneElement

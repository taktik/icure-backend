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
package org.taktik.icure.fhir.entities.r4.specimen

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.duration.Duration
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Collection details
 *
 * Details concerning the specimen collection.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SpecimenCollection(
  /**
   * Anatomical collection site
   */
  val bodySite: CodeableConcept? = null,
  /**
   * Collection time
   */
  val collectedDateTime: String? = null,
  /**
   * Collection time
   */
  val collectedPeriod: Period? = null,
  /**
   * Who collected the specimen
   */
  val collector: Reference? = null,
  /**
   * How long it took to collect specimen
   */
  val duration: Duration? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Whether or how long patient abstained from food and/or drink
   */
  val fastingStatusCodeableConcept: CodeableConcept? = null,
  /**
   * Whether or how long patient abstained from food and/or drink
   */
  val fastingStatusDuration: Duration? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Technique used to perform collection
   */
  val method: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The quantity of specimen collected
   */
  val quantity: Quantity? = null
) : BackboneElement

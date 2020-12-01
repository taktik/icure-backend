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
package org.taktik.icure.services.external.rest.fhir.dto.r4.claim

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.attachment.Attachment
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Supporting information
 *
 * Additional information codes regarding exceptions, special considerations, the condition,
 * situation, prior or concurrent issues.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimSupportingInfo(
  /**
   * Classification of the supplied information
   */
  val category: CodeableConcept,
  /**
   * Type of information
   */
  val code: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Explanation for the information
   */
  val reason: CodeableConcept? = null,
  /**
   * Information instance identifier
   */
  val sequence: Int? = null,
  /**
   * When it occurred
   */
  val timingDate: String? = null,
  /**
   * When it occurred
   */
  val timingPeriod: Period? = null,
  /**
   * Data to be provided
   */
  val valueAttachment: Attachment? = null,
  /**
   * Data to be provided
   */
  val valueBoolean: Boolean? = null,
  /**
   * Data to be provided
   */
  val valueQuantity: Quantity? = null,
  /**
   * Data to be provided
   */
  val valueReference: Reference? = null,
  /**
   * Data to be provided
   */
  val valueString: String? = null
) : BackboneElement

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
package org.taktik.icure.fhir.entities.r4.contract

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Response to offer text
 *
 * Response to offer text.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ContractTermOfferAnswer(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The actual answer response
   */
  val valueAttachment: Attachment,
  /**
   * The actual answer response
   */
  val valueBoolean: Boolean? = null,
  /**
   * The actual answer response
   */
  val valueCoding: Coding,
  /**
   * The actual answer response
   */
  val valueDate: String? = null,
  /**
   * The actual answer response
   */
  val valueDateTime: String? = null,
  /**
   * The actual answer response
   */
  val valueDecimal: Float? = null,
  /**
   * The actual answer response
   */
  val valueInteger: Int? = null,
  /**
   * The actual answer response
   */
  val valueQuantity: Quantity,
  /**
   * The actual answer response
   */
  val valueReference: Reference,
  /**
   * The actual answer response
   */
  val valueString: String? = null,
  /**
   * The actual answer response
   */
  val valueTime: String? = null,
  /**
   * The actual answer response
   */
  val valueUri: String? = null
) : BackboneElement

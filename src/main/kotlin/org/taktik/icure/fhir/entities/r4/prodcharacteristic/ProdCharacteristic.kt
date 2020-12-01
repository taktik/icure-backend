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
package org.taktik.icure.fhir.entities.r4.prodcharacteristic

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * The marketing status describes the date when a medicinal product is actually put on the market or
 * the date as of which it is no longer available
 *
 * The marketing status describes the date when a medicinal product is actually put on the market or
 * the date as of which it is no longer available.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ProdCharacteristic(
  val color: List<String> = listOf(),
  /**
   * Where applicable, the depth can be specified using a numerical value and its unit of
   * measurement The unit of measurement shall be specified in accordance with ISO 11240 and the
   * resulting terminology The symbol and the symbol identifier shall be used
   */
  val depth: Quantity? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Where applicable, the external diameter can be specified using a numerical value and its unit
   * of measurement The unit of measurement shall be specified in accordance with ISO 11240 and the
   * resulting terminology The symbol and the symbol identifier shall be used
   */
  val externalDiameter: Quantity? = null,
  /**
   * Where applicable, the height can be specified using a numerical value and its unit of
   * measurement The unit of measurement shall be specified in accordance with ISO 11240 and the
   * resulting terminology The symbol and the symbol identifier shall be used
   */
  val height: Quantity? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val image: List<Attachment> = listOf(),
  val imprint: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Where applicable, the nominal volume can be specified using a numerical value and its unit of
   * measurement The unit of measurement shall be specified in accordance with ISO 11240 and the
   * resulting terminology The symbol and the symbol identifier shall be used
   */
  val nominalVolume: Quantity? = null,
  /**
   * Where applicable, the scoring can be specified An appropriate controlled vocabulary shall be
   * used The term and the term identifier shall be used
   */
  val scoring: CodeableConcept? = null,
  /**
   * Where applicable, the shape can be specified An appropriate controlled vocabulary shall be used
   * The term and the term identifier shall be used
   */
  val shape: String? = null,
  /**
   * Where applicable, the weight can be specified using a numerical value and its unit of
   * measurement The unit of measurement shall be specified in accordance with ISO 11240 and the
   * resulting terminology The symbol and the symbol identifier shall be used
   */
  val weight: Quantity? = null,
  /**
   * Where applicable, the width can be specified using a numerical value and its unit of
   * measurement The unit of measurement shall be specified in accordance with ISO 11240 and the
   * resulting terminology The symbol and the symbol identifier shall be used
   */
  val width: Quantity? = null
) : BackboneElement

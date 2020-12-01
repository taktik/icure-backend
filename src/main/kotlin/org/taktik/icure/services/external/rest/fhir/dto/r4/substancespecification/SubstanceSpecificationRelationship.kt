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
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancespecification

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.ratio.Ratio
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A link between this substance and another, with details of the relationship
 *
 * A link between this substance and another, with details of the relationship.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSpecificationRelationship(
  /**
   * A numeric factor for the relationship, for instance to express that the salt of a substance has
   * some percentage of the active substance in relation to some other
   */
  val amountQuantity: Quantity? = null,
  /**
   * A numeric factor for the relationship, for instance to express that the salt of a substance has
   * some percentage of the active substance in relation to some other
   */
  val amountRange: Range? = null,
  /**
   * A numeric factor for the relationship, for instance to express that the salt of a substance has
   * some percentage of the active substance in relation to some other
   */
  val amountRatio: Ratio? = null,
  /**
   * For use when the numeric
   */
  val amountRatioLowLimit: Ratio? = null,
  /**
   * A numeric factor for the relationship, for instance to express that the salt of a substance has
   * some percentage of the active substance in relation to some other
   */
  val amountString: String? = null,
  /**
   * An operator for the amount, for example "average", "approximately", "less than"
   */
  val amountType: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * For example where an enzyme strongly bonds with a particular substance, this is a defining
   * relationship for that enzyme, out of several possible substance relationships
   */
  val isDefining: Boolean? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * For example "salt to parent", "active moiety", "starting material"
   */
  val relationship: CodeableConcept? = null,
  val source: List<Reference> = listOf(),
  /**
   * A pointer to another substance, as a resource or just a representational code
   */
  val substanceCodeableConcept: CodeableConcept? = null,
  /**
   * A pointer to another substance, as a resource or just a representational code
   */
  val substanceReference: Reference? = null
) : BackboneElement

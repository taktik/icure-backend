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
package org.taktik.icure.services.external.rest.fhir.dto.r4.explanationofbenefit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.address.Address
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Product or service provided
 *
 * A claim line. Either a simple (a product or service) or a 'group' of details which can also be a
 * simple items or groups of sub-details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefitItem(
  val adjudication: List<ExplanationOfBenefitItemAdjudication> = listOf(),
  /**
   * Anatomical location
   */
  val bodySite: CodeableConcept? = null,
  val careTeamSequence: List<Int> = listOf(),
  /**
   * Benefit classification
   */
  val category: CodeableConcept? = null,
  val detail: List<ExplanationOfBenefitItemDetail> = listOf(),
  val diagnosisSequence: List<Int> = listOf(),
  val encounter: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Price scaling factor
   */
  val factor: Float? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val informationSequence: List<Int> = listOf(),
  /**
   * Place of service or where product was supplied
   */
  val locationAddress: Address? = null,
  /**
   * Place of service or where product was supplied
   */
  val locationCodeableConcept: CodeableConcept? = null,
  /**
   * Place of service or where product was supplied
   */
  val locationReference: Reference? = null,
  val modifier: List<CodeableConcept> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Total item cost
   */
  val net: Money? = null,
  val noteNumber: List<Int> = listOf(),
  val procedureSequence: List<Int> = listOf(),
  /**
   * Billing, service, product, or drug code
   */
  val productOrService: CodeableConcept,
  val programCode: List<CodeableConcept> = listOf(),
  /**
   * Count of products or services
   */
  val quantity: Quantity? = null,
  /**
   * Revenue or cost center code
   */
  val revenue: CodeableConcept? = null,
  /**
   * Item instance identifier
   */
  val sequence: Int? = null,
  /**
   * Date or dates of service or product delivery
   */
  val servicedDate: String? = null,
  /**
   * Date or dates of service or product delivery
   */
  val servicedPeriod: Period? = null,
  val subSite: List<CodeableConcept> = listOf(),
  val udi: List<Reference> = listOf(),
  /**
   * Fee, charge or cost per item
   */
  val unitPrice: Money? = null
) : BackboneElement

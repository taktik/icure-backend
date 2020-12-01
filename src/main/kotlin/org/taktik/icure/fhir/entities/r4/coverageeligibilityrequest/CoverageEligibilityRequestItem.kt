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
package org.taktik.icure.fhir.entities.r4.coverageeligibilityrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.money.Money
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Item to be evaluated for eligibiity
 *
 * Service categories or billable services for which benefit details and/or an authorization prior
 * to service delivery may be required by the payor.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CoverageEligibilityRequestItem(
  /**
   * Benefit classification
   */
  val category: CodeableConcept? = null,
  val detail: List<Reference> = listOf(),
  val diagnosis: List<CoverageEligibilityRequestItemDiagnosis> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Servicing facility
   */
  val facility: Reference? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val modifier: List<CodeableConcept> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Billing, service, product, or drug code
   */
  val productOrService: CodeableConcept? = null,
  /**
   * Perfoming practitioner
   */
  val provider: Reference? = null,
  /**
   * Count of products or services
   */
  val quantity: Quantity? = null,
  val supportingInfoSequence: List<Int> = listOf(),
  /**
   * Fee, charge or cost per item
   */
  val unitPrice: Money? = null
) : BackboneElement

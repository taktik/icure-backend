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
package org.taktik.icure.services.external.rest.fhir.dto.r4.marketingstatus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period

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
data class MarketingStatus(
  /**
   * The country in which the marketing authorisation has been granted shall be specified It should
   * be specified using the ISO 3166 ‑ 1 alpha-2 code elements
   */
  val country: CodeableConcept,
  /**
   * The date when the Medicinal Product is placed on the market by the Marketing Authorisation
   * Holder (or where applicable, the manufacturer/distributor) in a country and/or jurisdiction shall
   * be provided A complete date consisting of day, month and year shall be specified using the ISO
   * 8601 date format NOTE “Placed on the market” refers to the release of the Medicinal Product into
   * the distribution chain
   */
  val dateRange: Period,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Where a Medicines Regulatory Agency has granted a marketing authorisation for which specific
   * provisions within a jurisdiction apply, the jurisdiction can be specified using an appropriate
   * controlled terminology The controlled term and the controlled term identifier shall be specified
   */
  val jurisdiction: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The date when the Medicinal Product is placed on the market by the Marketing Authorisation
   * Holder (or where applicable, the manufacturer/distributor) in a country and/or jurisdiction shall
   * be provided A complete date consisting of day, month and year shall be specified using the ISO
   * 8601 date format NOTE “Placed on the market” refers to the release of the Medicinal Product into
   * the distribution chain
   */
  val restoreDate: String? = null,
  /**
   * This attribute provides information on the status of the marketing of the medicinal product See
   * ISO/TS 20443 for more information and examples
   */
  val status: CodeableConcept
) : BackboneElement

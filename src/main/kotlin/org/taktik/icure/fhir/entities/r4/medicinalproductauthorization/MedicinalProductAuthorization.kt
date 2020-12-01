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
package org.taktik.icure.fhir.entities.r4.medicinalproductauthorization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * The regulatory authorization of a medicinal product
 *
 * The regulatory authorization of a medicinal product.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductAuthorization(
  override val contained: List<Resource> = listOf(),
  val country: List<CodeableConcept> = listOf(),
  /**
   * A period of time after authorization before generic product applicatiosn can be submitted
   */
  val dataExclusivityPeriod: Period? = null,
  /**
   * The date when the first authorization was granted by a Medicines Regulatory Agency
   */
  val dateOfFirstAuthorization: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Marketing Authorization Holder
   */
  val holder: Reference? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Date of first marketing authorization for a company's new medicinal product in any country in
   * the World
   */
  val internationalBirthDate: String? = null,
  val jurisdiction: List<CodeableConcept> = listOf(),
  val jurisdictionalAuthorization: List<MedicinalProductAuthorizationJurisdictionalAuthorization> =
      listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * The legal framework against which this authorization is granted
   */
  val legalBasis: CodeableConcept? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The regulatory procedure for granting or amending a marketing authorization
   */
  val procedure: MedicinalProductAuthorizationProcedure? = null,
  /**
   * Medicines Regulatory Agency
   */
  val regulator: Reference? = null,
  /**
   * The date when a suspended the marketing or the marketing authorization of the product is
   * anticipated to be restored
   */
  val restoreDate: String? = null,
  /**
   * The status of the marketing authorization
   */
  val status: CodeableConcept? = null,
  /**
   * The date at which the given status has become applicable
   */
  val statusDate: String? = null,
  /**
   * The medicinal product that is being authorized
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * The beginning of the time period in which the marketing authorization is in the specific status
   * shall be specified A complete date consisting of day, month and year shall be specified using the
   * ISO 8601 date format
   */
  val validityPeriod: Period? = null
) : DomainResource

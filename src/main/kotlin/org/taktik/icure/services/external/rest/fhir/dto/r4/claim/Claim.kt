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
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Claim, Pre-determination or Pre-authorization
 *
 * A provider issued list of professional services and products which have been provided, or are to
 * be provided, to a patient which is sent to an insurer for reimbursement.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Claim(
  /**
   * Details of the event
   */
  val accident: ClaimAccident? = null,
  /**
   * Relevant time frame for the claim
   */
  val billablePeriod: Period? = null,
  val careTeam: List<ClaimCareTeam> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Resource creation date
   */
  val created: String? = null,
  val diagnosis: List<ClaimDiagnosis> = listOf(),
  /**
   * Author of the claim
   */
  val enterer: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Servicing facility
   */
  val facility: Reference? = null,
  /**
   * For whom to reserve funds
   */
  val fundsReserve: CodeableConcept? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val insurance: List<ClaimInsurance> = listOf(),
  /**
   * Target
   */
  val insurer: Reference? = null,
  val item: List<ClaimItem> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Original prescription if superseded by fulfiller
   */
  val originalPrescription: Reference? = null,
  /**
   * The recipient of the products and services
   */
  val patient: Reference,
  /**
   * Recipient of benefits payable
   */
  val payee: ClaimPayee? = null,
  /**
   * Prescription authorizing services and products
   */
  val prescription: Reference? = null,
  /**
   * Desired processing ugency
   */
  val priority: CodeableConcept,
  val procedure: List<ClaimProcedure> = listOf(),
  /**
   * Party responsible for the claim
   */
  val provider: Reference,
  /**
   * Treatment referral
   */
  val referral: Reference? = null,
  val related: List<ClaimRelated> = listOf(),
  /**
   * active | cancelled | draft | entered-in-error
   */
  val status: String? = null,
  /**
   * More granular claim type
   */
  val subType: CodeableConcept? = null,
  val supportingInfo: List<ClaimSupportingInfo> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Total claim cost
   */
  val total: Money? = null,
  /**
   * Category or discipline
   */
  val type: CodeableConcept,
  /**
   * claim | preauthorization | predetermination
   */
  val use: String? = null
) : DomainResource

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
package org.taktik.icure.fhir.entities.r4.explanationofbenefit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Explanation of Benefit resource
 *
 * This resource provides: the claim details; adjudication details from the processing of a Claim;
 * and optionally account balance information, for informing the subscriber of the benefits provided.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefit(
  /**
   * Details of the event
   */
  val accident: ExplanationOfBenefitAccident? = null,
  val addItem: List<ExplanationOfBenefitAddItem> = listOf(),
  val adjudication: List<ExplanationOfBenefitItemAdjudication> = listOf(),
  val benefitBalance: List<ExplanationOfBenefitBenefitBalance> = listOf(),
  /**
   * When the benefits are applicable
   */
  val benefitPeriod: Period? = null,
  /**
   * Relevant time frame for the claim
   */
  val billablePeriod: Period? = null,
  val careTeam: List<ExplanationOfBenefitCareTeam> = listOf(),
  /**
   * Claim reference
   */
  val claim: Reference? = null,
  /**
   * Claim response reference
   */
  val claimResponse: Reference? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Response creation date
   */
  val created: String? = null,
  val diagnosis: List<ExplanationOfBenefitDiagnosis> = listOf(),
  /**
   * Disposition Message
   */
  val disposition: String? = null,
  /**
   * Author of the claim
   */
  val enterer: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Servicing Facility
   */
  val facility: Reference? = null,
  /**
   * Printed reference or actual form
   */
  val form: Attachment? = null,
  /**
   * Printed form identifier
   */
  val formCode: CodeableConcept? = null,
  /**
   * Funds reserved status
   */
  val fundsReserve: CodeableConcept? = null,
  /**
   * For whom to reserve funds
   */
  val fundsReserveRequested: CodeableConcept? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val insurance: List<ExplanationOfBenefitInsurance> = listOf(),
  /**
   * Party responsible for reimbursement
   */
  val insurer: Reference,
  val item: List<ExplanationOfBenefitItem> = listOf(),
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
   * Original prescription if superceded by fulfiller
   */
  val originalPrescription: Reference? = null,
  /**
   * queued | complete | error | partial
   */
  val outcome: String? = null,
  /**
   * The recipient of the products and services
   */
  val patient: Reference,
  /**
   * Recipient of benefits payable
   */
  val payee: ExplanationOfBenefitPayee? = null,
  /**
   * Payment Details
   */
  val payment: ExplanationOfBenefitPayment? = null,
  val preAuthRef: List<String> = listOf(),
  val preAuthRefPeriod: List<Period> = listOf(),
  /**
   * Precedence (primary, secondary, etc.)
   */
  val precedence: Int? = null,
  /**
   * Prescription authorizing services or products
   */
  val prescription: Reference? = null,
  /**
   * Desired processing urgency
   */
  val priority: CodeableConcept? = null,
  val procedure: List<ExplanationOfBenefitProcedure> = listOf(),
  val processNote: List<ExplanationOfBenefitProcessNote> = listOf(),
  /**
   * Party responsible for the claim
   */
  val provider: Reference,
  /**
   * Treatment Referral
   */
  val referral: Reference? = null,
  val related: List<ExplanationOfBenefitRelated> = listOf(),
  /**
   * active | cancelled | draft | entered-in-error
   */
  val status: String? = null,
  /**
   * More granular claim type
   */
  val subType: CodeableConcept? = null,
  val supportingInfo: List<ExplanationOfBenefitSupportingInfo> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val total: List<ExplanationOfBenefitTotal> = listOf(),
  /**
   * Category or discipline
   */
  val type: CodeableConcept,
  /**
   * claim | preauthorization | predetermination
   */
  val use: String? = null
) : DomainResource

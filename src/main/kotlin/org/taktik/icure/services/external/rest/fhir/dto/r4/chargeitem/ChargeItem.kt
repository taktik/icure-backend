//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.chargeitem

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing

/**
 * Item containing charge code(s) associated with the provision of healthcare provider products
 *
 * The resource ChargeItem describes the provision of healthcare provider products for a certain
 * patient, therefore referring not only to the product, but containing in addition details of the
 * provision, like date, time, amounts and participating organizations and persons. Main Usage of the
 * ChargeItem is to enable the billing process and internal cost allocation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ChargeItem(
  val account: List<Reference> = listOf(),
  val bodysite: List<CodeableConcept> = listOf(),
  /**
   * A code that identifies the charge, like a billing code
   */
  val code: CodeableConcept,
  override val contained: List<Resource> = listOf(),
  /**
   * Encounter / Episode associated with event
   */
  val context: Reference? = null,
  /**
   * Organization that has ownership of the (potential, future) revenue
   */
  val costCenter: Reference? = null,
  val definitionCanonical: List<String> = listOf(),
  val definitionUri: List<String> = listOf(),
  /**
   * Date the charge item was entered
   */
  val enteredDate: String? = null,
  /**
   * Individual who was entering
   */
  val enterer: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Factor overriding the associated rules
   */
  val factorOverride: Float? = null,
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
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * When the charged service was applied
   */
  val occurrenceDateTime: String? = null,
  /**
   * When the charged service was applied
   */
  val occurrencePeriod: Period? = null,
  /**
   * When the charged service was applied
   */
  val occurrenceTiming: Timing? = null,
  /**
   * Reason for overriding the list price/factor
   */
  val overrideReason: String? = null,
  val partOf: List<Reference> = listOf(),
  val performer: List<ChargeItemPerformer> = listOf(),
  /**
   * Organization providing the charged service
   */
  val performingOrganization: Reference? = null,
  /**
   * Price overriding the associated rules
   */
  val priceOverride: Money? = null,
  /**
   * Product charged
   */
  val productCodeableConcept: CodeableConcept? = null,
  /**
   * Product charged
   */
  val productReference: Reference? = null,
  /**
   * Quantity of which the charge item has been serviced
   */
  val quantity: Quantity? = null,
  val reason: List<CodeableConcept> = listOf(),
  /**
   * Organization requesting the charged service
   */
  val requestingOrganization: Reference? = null,
  val service: List<Reference> = listOf(),
  /**
   * planned | billable | not-billable | aborted | billed | entered-in-error | unknown
   */
  val status: String? = null,
  /**
   * Individual service was done for/to
   */
  val subject: Reference,
  val supportingInformation: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

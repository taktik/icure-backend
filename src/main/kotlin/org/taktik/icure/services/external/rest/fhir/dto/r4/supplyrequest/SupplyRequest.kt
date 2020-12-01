//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.supplyrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing

/**
 * Request for a medication, substance or device
 *
 * A record of a request for a medication, substance or device used in the healthcare setting.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SupplyRequest(
  /**
   * When the request was made
   */
  val authoredOn: String? = null,
  /**
   * The kind of supply (central, non-stock, etc.)
   */
  val category: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * The origin of the supply
   */
  val deliverFrom: Reference? = null,
  /**
   * The destination of the supply
   */
  val deliverTo: Reference? = null,
  override val extension: List<Extension> = listOf(),
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
   * Medication, Substance, or Device requested to be supplied
   */
  val itemCodeableConcept: CodeableConcept,
  /**
   * Medication, Substance, or Device requested to be supplied
   */
  val itemReference: Reference,
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
   * When the request should be fulfilled
   */
  val occurrenceDateTime: String? = null,
  /**
   * When the request should be fulfilled
   */
  val occurrencePeriod: Period? = null,
  /**
   * When the request should be fulfilled
   */
  val occurrenceTiming: Timing? = null,
  val parameter: List<SupplyRequestParameter> = listOf(),
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  /**
   * The requested amount of the item indicated
   */
  val quantity: Quantity,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * Individual making the request
   */
  val requester: Reference? = null,
  /**
   * draft | active | suspended +
   */
  val status: String? = null,
  val supplier: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

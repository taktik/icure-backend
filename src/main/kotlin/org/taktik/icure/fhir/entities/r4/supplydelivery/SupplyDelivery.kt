//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.supplydelivery

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
import org.taktik.icure.fhir.entities.r4.timing.Timing

/**
 * Delivery of bulk Supplies
 *
 * Record of delivery of what is supplied.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SupplyDelivery(
  val basedOn: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Where the Supply was sent
   */
  val destination: Reference? = null,
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
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * When event occurred
   */
  val occurrenceDateTime: String? = null,
  /**
   * When event occurred
   */
  val occurrencePeriod: Period? = null,
  /**
   * When event occurred
   */
  val occurrenceTiming: Timing? = null,
  val partOf: List<Reference> = listOf(),
  /**
   * Patient for whom the item is supplied
   */
  val patient: Reference? = null,
  val receiver: List<Reference> = listOf(),
  /**
   * in-progress | completed | abandoned | entered-in-error
   */
  val status: String? = null,
  /**
   * The item that is delivered or supplied
   */
  val suppliedItem: SupplyDeliverySuppliedItem? = null,
  /**
   * Dispenser
   */
  val supplier: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Category of dispense event
   */
  val type: CodeableConcept? = null
) : DomainResource

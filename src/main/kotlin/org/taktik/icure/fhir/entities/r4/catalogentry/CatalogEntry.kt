//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.catalogentry

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
 * An entry in a catalog
 *
 * Catalog entries are wrappers that contextualize items included in a catalog.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CatalogEntry(
  val additionalCharacteristic: List<CodeableConcept> = listOf(),
  val additionalClassification: List<CodeableConcept> = listOf(),
  val additionalIdentifier: List<Identifier> = listOf(),
  val classification: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
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
   * When was this catalog last updated
   */
  val lastUpdated: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Whether the entry represents an orderable item
   */
  val orderable: Boolean? = null,
  /**
   * The item that is being defined
   */
  val referencedItem: Reference,
  val relatedEntry: List<CatalogEntryRelatedEntry> = listOf(),
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * The type of item - medication, device, service, protocol or other
   */
  val type: CodeableConcept? = null,
  /**
   * The date until which this catalog entry is expected to be active
   */
  val validTo: String? = null,
  /**
   * The time period in which this catalog entry is expected to be active
   */
  val validityPeriod: Period? = null
) : DomainResource

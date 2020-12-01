//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.conceptmap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactdetail.ContactDetail
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.usagecontext.UsageContext

/**
 * A map from one set of concepts to one or more other concepts
 *
 * A statement of relationships from one set of concepts to one or more other concepts - either
 * concepts in code systems, or data element/data element concepts, or classes in class models.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ConceptMap(
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Use and/or publishing restrictions
   */
  val copyright: String? = null,
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Natural language description of the concept map
   */
  val description: String? = null,
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  val group: List<ConceptMapGroup> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * Additional identifier for the concept map
   */
  val identifier: Identifier? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val jurisdiction: List<CodeableConcept> = listOf(),
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
   * Name for this concept map (computer friendly)
   */
  val name: String? = null,
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this concept map is defined
   */
  val purpose: String? = null,
  /**
   * The source value set that contains the concepts that are being mapped
   */
  val sourceCanonical: String? = null,
  /**
   * The source value set that contains the concepts that are being mapped
   */
  val sourceUri: String? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * The target value set which provides context for the mappings
   */
  val targetCanonical: String? = null,
  /**
   * The target value set which provides context for the mappings
   */
  val targetUri: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this concept map (human friendly)
   */
  val title: String? = null,
  /**
   * Canonical identifier for this concept map, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the concept map
   */
  val version: String? = null
) : DomainResource

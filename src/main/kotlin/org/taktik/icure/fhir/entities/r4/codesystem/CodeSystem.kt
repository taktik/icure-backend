//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.codesystem

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * Declares the existence of and describes a code system or code system supplement
 *
 * The CodeSystem resource is used to declare the existence of and describe a code system or code
 * system supplement and its key properties, and optionally define a part or all of its content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CodeSystem(
  /**
   * If code comparison is case sensitive
   */
  val caseSensitive: Boolean? = null,
  /**
   * If code system defines a compositional grammar
   */
  val compositional: Boolean? = null,
  val concept: List<CodeSystemConcept> = listOf(),
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * not-present | example | fragment | complete | supplement
   */
  val content: String? = null,
  /**
   * Use and/or publishing restrictions
   */
  val copyright: String? = null,
  /**
   * Total concepts in the code system
   */
  val count: Int? = null,
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Natural language description of the code system
   */
  val description: String? = null,
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  val filter: List<CodeSystemFilter> = listOf(),
  /**
   * grouped-by | is-a | part-of | classified-with
   */
  val hierarchyMeaning: String? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
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
   * Name for this code system (computer friendly)
   */
  val name: String? = null,
  val property: List<CodeSystemProperty> = listOf(),
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this code system is defined
   */
  val purpose: String? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Canonical URL of Code System this adds designations and properties to
   */
  val supplements: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this code system (human friendly)
   */
  val title: String? = null,
  /**
   * Canonical identifier for this code system, represented as a URI (globally unique)
   * (Coding.system)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Canonical reference to the value set with entire code system
   */
  val valueSet: String? = null,
  /**
   * Business version of the code system (Coding.version)
   */
  val version: String? = null,
  /**
   * If definitions are not stable
   */
  val versionNeeded: Boolean? = null
) : DomainResource

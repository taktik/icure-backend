//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.operationdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * Definition of an operation or a named query
 *
 * A formal computable definition of an operation (on the RESTful interface) or a named query (using
 * the search interaction).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class OperationDefinition(
  /**
   * Whether content is changed by the operation
   */
  val affectsState: Boolean? = null,
  /**
   * Marks this as a profile of the base
   */
  val base: String? = null,
  /**
   * Name used to invoke the operation
   */
  val code: String? = null,
  /**
   * Additional information about use
   */
  val comment: String? = null,
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Natural language description of the operation definition
   */
  val description: String? = null,
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Validation information for in parameters
   */
  val inputProfile: String? = null,
  /**
   * Invoke on an instance?
   */
  val instance: Boolean? = null,
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * operation | query
   */
  val kind: String? = null,
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
   * Name for this operation definition (computer friendly)
   */
  val name: String? = null,
  /**
   * Validation information for out parameters
   */
  val outputProfile: String? = null,
  val overload: List<OperationDefinitionOverload> = listOf(),
  val parameter: List<OperationDefinitionParameter> = listOf(),
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this operation definition is defined
   */
  val purpose: String? = null,
  val resource: List<String> = listOf(),
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Invoke at the system level?
   */
  val system: Boolean? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this operation definition (human friendly)
   */
  val title: String? = null,
  /**
   * Invoke at the type level?
   */
  val type: Boolean? = null,
  /**
   * Canonical identifier for this operation definition, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the operation definition
   */
  val version: String? = null
) : DomainResource

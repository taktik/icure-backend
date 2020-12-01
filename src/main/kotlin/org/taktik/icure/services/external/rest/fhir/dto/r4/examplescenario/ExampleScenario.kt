//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.examplescenario

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
 * Example of workflow instance
 *
 * Example of workflow instance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExampleScenario(
  val actor: List<ExampleScenarioActor> = listOf(),
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
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
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
  val instance: List<ExampleScenarioInstance> = listOf(),
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
   * Name for this example scenario (computer friendly)
   */
  val name: String? = null,
  val process: List<ExampleScenarioProcess> = listOf(),
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * The purpose of the example, e.g. to illustrate a scenario
   */
  val purpose: String? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Canonical identifier for this example scenario, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the example scenario
   */
  val version: String? = null,
  val workflow: List<String> = listOf()
) : DomainResource

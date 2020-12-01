//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.examplescenario

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Each resource and each version that is present in the workflow
 *
 * Each resource and each version that is present in the workflow.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExampleScenarioInstance(
  val containedInstance: List<ExampleScenarioInstanceContainedInstance> = listOf(),
  /**
   * Human-friendly description of the resource instance
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * A short name for the resource instance
   */
  val name: String? = null,
  /**
   * The id of the resource for referencing
   */
  val resourceId: String? = null,
  /**
   * The type of the resource
   */
  val resourceType: String? = null,
  val version: List<ExampleScenarioInstanceVersion> = listOf()
) : BackboneElement

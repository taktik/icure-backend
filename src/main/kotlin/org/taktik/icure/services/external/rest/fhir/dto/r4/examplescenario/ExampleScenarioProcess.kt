//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.examplescenario

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Each major process - a group of operations
 *
 * Each major process - a group of operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExampleScenarioProcess(
  /**
   * A longer description of the group of operations
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Description of final status after the process ends
   */
  val postConditions: String? = null,
  /**
   * Description of initial status before the process starts
   */
  val preConditions: String? = null,
  val step: List<ExampleScenarioProcessStep> = listOf(),
  /**
   * The diagram title of the group of operations
   */
  val title: String? = null
) : BackboneElement

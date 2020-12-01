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
 * Each interaction or action
 *
 * Each interaction or action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExampleScenarioProcessStepOperation(
  /**
   * A comment to be inserted in the diagram
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Who starts the transaction
   */
  val initiator: String? = null,
  /**
   * Whether the initiator is deactivated right after the transaction
   */
  val initiatorActive: Boolean? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The human-friendly name of the interaction
   */
  val name: String? = null,
  /**
   * The sequential number of the interaction
   */
  val number: String? = null,
  /**
   * Who receives the transaction
   */
  val receiver: String? = null,
  /**
   * Whether the receiver is deactivated right after the transaction
   */
  val receiverActive: Boolean? = null,
  /**
   * Each resource instance used by the initiator
   */
  val request: ExampleScenarioInstanceContainedInstance? = null,
  /**
   * Each resource instance used by the responder
   */
  val response: ExampleScenarioInstanceContainedInstance? = null,
  /**
   * The type of operation - CRUD
   */
  val type: String? = null
) : BackboneElement

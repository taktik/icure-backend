//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.task

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Constraints on fulfillment tasks
 *
 * If the Task.focus is a request resource and the task is seeking fulfillment (i.e. is asking for
 * the request to be actioned), this element identifies any limitations on what parts of the referenced
 * request should be actioned.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TaskRestriction(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * When fulfillment sought
   */
  val period: Period? = null,
  val recipient: List<Reference> = listOf(),
  /**
   * How many times to repeat
   */
  val repetitions: Int? = null
) : BackboneElement

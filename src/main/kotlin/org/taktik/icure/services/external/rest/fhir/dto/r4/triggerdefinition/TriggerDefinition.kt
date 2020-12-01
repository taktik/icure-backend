//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.triggerdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.datarequirement.DataRequirement
import org.taktik.icure.services.external.rest.fhir.dto.r4.expression.Expression
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing

/**
 * Defines an expected trigger for a module
 *
 * A description of a triggering event. Triggering events can be named events, data events, or
 * periodic, as determined by the type element.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TriggerDefinition(
  /**
   * Whether the event triggers (boolean expression)
   */
  val condition: Expression? = null,
  val data: List<DataRequirement> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Name or URI that identifies the event
   */
  val name: String? = null,
  /**
   * Timing of the event
   */
  val timingDate: String? = null,
  /**
   * Timing of the event
   */
  val timingDateTime: String? = null,
  /**
   * Timing of the event
   */
  val timingReference: Reference? = null,
  /**
   * Timing of the event
   */
  val timingTiming: Timing? = null,
  /**
   * named-event | periodic | data-changed | data-added | data-modified | data-removed |
   * data-accessed | data-access-ended
   */
  val type: String? = null
) : Element

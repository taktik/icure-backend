//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.messagedefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Resource(s) that are the subject of the event
 *
 * Identifies the resource (or resources) that are being addressed by the event.  For example, the
 * Encounter for an admit message or two Account records for a merge.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MessageDefinitionFocus(
  /**
   * Type of resource
   */
  val code: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Maximum number of focuses of this type
   */
  val max: String? = null,
  /**
   * Minimum number of focuses of this type
   */
  val min: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Profile that must be adhered to by focus
   */
  val profile: String? = null
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.messageheader

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * If this is a reply to prior message
 *
 * Information about the message that this message is a response to.  Only present if this message
 * is a response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MessageHeaderResponse(
  /**
   * ok | transient-error | fatal-error
   */
  val code: String? = null,
  /**
   * Specific list of hints/warnings/errors
   */
  val details: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Id of original message
   */
  val identifier: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

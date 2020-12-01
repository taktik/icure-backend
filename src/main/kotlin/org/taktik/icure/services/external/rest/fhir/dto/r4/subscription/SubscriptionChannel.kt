//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.subscription

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The channel on which to report matches to the criteria
 *
 * Details where to send notifications when resources are received that meet the criteria.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubscriptionChannel(
  /**
   * Where the channel points to
   */
  val endpoint: String? = null,
  override val extension: List<Extension> = listOf(),
  val header: List<String> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * MIME type to send, or omit for no payload
   */
  val payload: String? = null,
  /**
   * rest-hook | websocket | email | sms | message
   */
  val type: String? = null
) : BackboneElement

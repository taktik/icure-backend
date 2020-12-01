//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.capabilitystatement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * If messaging is supported
 *
 * A description of the messaging capabilities of the solution.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CapabilityStatementMessaging(
  /**
   * Messaging interface behavior details
   */
  val documentation: String? = null,
  val endpoint: List<CapabilityStatementMessagingEndpoint> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Reliable Message Cache Length (min)
   */
  val reliableCache: Int? = null,
  val supportedMessage: List<CapabilityStatementMessagingSupportedMessage> = listOf()
) : BackboneElement

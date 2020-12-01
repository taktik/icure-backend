//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.subscription

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.contactpoint.ContactPoint
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative

/**
 * Server push subscription criteria
 *
 * The subscription resource is used to define a push-based subscription from a server to another
 * system. Once a subscription is registered with the server, the server checks every resource that is
 * created or updated, and if the resource matches the given criteria, it sends a message on the
 * defined "channel" so that another system can take an appropriate action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Subscription(
  /**
   * The channel on which to report matches to the criteria
   */
  val channel: SubscriptionChannel,
  val contact: List<ContactPoint> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Rule for server push
   */
  val criteria: String? = null,
  /**
   * When to automatically delete the subscription
   */
  val end: String? = null,
  /**
   * Latest error note
   */
  val error: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
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
   * Description of why this subscription was created
   */
  val reason: String? = null,
  /**
   * requested | active | error | off
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

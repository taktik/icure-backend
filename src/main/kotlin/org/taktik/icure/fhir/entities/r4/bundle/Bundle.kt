//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.bundle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.signature.Signature

/**
 * Contains a collection of resources
 *
 * A container for a collection of resources.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Bundle(
  val entry: List<BundleEntry> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * Persistent identifier for the bundle
   */
  val identifier: Identifier? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val link: List<BundleLink> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  /**
   * Digital Signature
   */
  val signature: Signature? = null,
  /**
   * When the bundle was assembled
   */
  val timestamp: String? = null,
  /**
   * If search, the total number of matches
   */
  val total: Int? = null,
  /**
   * document | message | transaction | transaction-response | batch | batch-response | history |
   * searchset | collection
   */
  val type: String? = null
) : Resource

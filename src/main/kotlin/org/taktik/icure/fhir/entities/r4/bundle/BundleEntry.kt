//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.bundle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Entry in the bundle - will have a resource or information
 *
 * An entry in a bundle resource - will either contain a resource or information about a resource
 * (transactions and history only).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class BundleEntry(
  override val extension: List<Extension> = listOf(),
  /**
   * URI for resource (Absolute URL server address or URI for UUID/OID)
   */
  val fullUrl: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val link: List<BundleLink> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Additional execution information (transaction/batch/history)
   */
  val request: BundleEntryRequest? = null,
  /**
   * A resource in the bundle
   */
  val resource: Resource? = null,
  /**
   * Results of execution (transaction/batch/history)
   */
  val response: BundleEntryResponse? = null,
  /**
   * Search related information
   */
  val search: BundleEntrySearch? = null
) : BackboneElement

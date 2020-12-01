//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.bundle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Additional execution information (transaction/batch/history)
 *
 * Additional information about how this entry should be processed as part of a transaction or
 * batch.  For history, it shows how the entry was processed to create the version contained in the
 * entry.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class BundleEntryRequest(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * For managing update contention
   */
  val ifMatch: String? = null,
  /**
   * For managing cache currency
   */
  val ifModifiedSince: String? = null,
  /**
   * For conditional creates
   */
  val ifNoneExist: String? = null,
  /**
   * For managing cache currency
   */
  val ifNoneMatch: String? = null,
  /**
   * GET | HEAD | POST | PUT | DELETE | PATCH
   */
  val method: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * URL for HTTP equivalent of this entry
   */
  val url: String? = null
) : BackboneElement

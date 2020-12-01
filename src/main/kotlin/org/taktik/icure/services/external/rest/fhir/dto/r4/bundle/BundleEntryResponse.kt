//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.bundle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Results of execution (transaction/batch/history)
 *
 * Indicates the results of processing the corresponding 'request' entry in the batch or transaction
 * being responded to or what the results of an operation where when returning history.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class BundleEntryResponse(
  /**
   * The Etag for the resource (if relevant)
   */
  val etag: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Server's date time modified
   */
  val lastModified: String? = null,
  /**
   * The location (if the operation returns a location)
   */
  val location: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * OperationOutcome with hints and warnings (for batch/transaction)
   */
  val outcome: Resource? = null,
  /**
   * Status response code (text optional)
   */
  val status: String? = null
) : BackboneElement

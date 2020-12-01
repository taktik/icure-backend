//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Metadata about a resource
 *
 * The metadata about a resource. This is content in the resource that is maintained by the
 * infrastructure. Changes to the content might not always be associated with version changes to the
 * resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Meta(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * When the resource version last changed
   */
  val lastUpdated: String? = null,
  val profile: List<String> = listOf(),
  val security: List<Coding> = listOf(),
  /**
   * Identifies where the resource comes from
   */
  val source: String? = null,
  val tag: List<Coding> = listOf(),
  /**
   * Version specific identifier
   */
  val versionId: String? = null
) : Element

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.attachment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Content in a format defined elsewhere
 *
 * For referring to data content defined in other formats.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Attachment(
  /**
   * Mime type of the content, with charset etc.
   */
  val contentType: String? = null,
  /**
   * Date attachment was first created
   */
  val creation: String? = null,
  /**
   * Data inline, base64ed
   */
  val data: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Hash of the data (sha-1, base64ed)
   */
  val hash: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Human language of the content (BCP-47)
   */
  val language: String? = null,
  /**
   * Number of bytes of content (if url provided)
   */
  val size: Int? = null,
  /**
   * Label to display in place of the data
   */
  val title: String? = null,
  /**
   * Uri where the data can be found
   */
  val url: String? = null
) : Element

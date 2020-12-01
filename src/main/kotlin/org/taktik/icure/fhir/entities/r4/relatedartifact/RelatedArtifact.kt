//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.relatedartifact

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Related artifacts for a knowledge resource
 *
 * Related artifacts such as additional documentation, justification, or bibliographic references.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class RelatedArtifact(
  /**
   * Bibliographic citation for the artifact
   */
  val citation: String? = null,
  /**
   * Brief description of the related artifact
   */
  val display: String? = null,
  /**
   * What document is being referenced
   */
  val document: Attachment? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Short label
   */
  val label: String? = null,
  /**
   * What resource is being referenced
   */
  val resource: String? = null,
  /**
   * documentation | justification | citation | predecessor | successor | derived-from | depends-on
   * | composed-of
   */
  val type: String? = null,
  /**
   * Where the artifact can be accessed
   */
  val url: String? = null
) : Element

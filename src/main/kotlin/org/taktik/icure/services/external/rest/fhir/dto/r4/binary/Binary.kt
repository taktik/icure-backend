//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.binary

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Pure binary content defined by a format other than FHIR
 *
 * A resource that represents the data of a single raw artifact as digital content accessible in its
 * native format.  A Binary resource can contain any content, whether text, image, pdf, zip archive,
 * etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Binary(
  /**
   * MimeType of the binary content
   */
  val contentType: String? = null,
  /**
   * The actual content
   */
  val data: String? = null,
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
  /**
   * Identifies another resource to use as proxy when enforcing access control
   */
  val securityContext: Reference? = null
) : Resource

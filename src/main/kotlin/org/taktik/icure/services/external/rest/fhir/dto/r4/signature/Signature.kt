//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.signature

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A Signature - XML DigSig, JWS, Graphical image of signature, etc.
 *
 * A signature along with supporting context. The signature may be a digital signature that is
 * cryptographic in nature, or some other signature acceptable to the domain. This other signature may
 * be as simple as a graphical image representing a hand-written signature, or a signature ceremony
 * Different signature approaches have different utilities.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Signature(
  /**
   * The actual signature content (XML DigSig. JWS, picture, etc.)
   */
  val data: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The party represented
   */
  val onBehalfOf: Reference? = null,
  /**
   * The technical format of the signature
   */
  val sigFormat: String? = null,
  /**
   * The technical format of the signed resources
   */
  val targetFormat: String? = null,
  val type: List<Coding> = listOf(),
  /**
   * When the signature was created
   */
  @JsonProperty("when")
  val when_fhir: String? = null,
  /**
   * Who signed
   */
  val who: Reference
) : Element

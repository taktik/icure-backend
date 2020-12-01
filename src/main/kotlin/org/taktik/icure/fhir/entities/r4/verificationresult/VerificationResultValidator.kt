//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.verificationresult

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.signature.Signature

/**
 * Information about the entity validating information
 *
 * Information about the entity validating information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VerificationResultValidator(
  /**
   * Validator signature
   */
  val attestationSignature: Signature? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * A digital identity certificate associated with the validator
   */
  val identityCertificate: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Reference to the organization validating information
   */
  val organization: Reference
) : BackboneElement

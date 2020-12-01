//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.verificationresult

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.signature.Signature

/**
 * Information about the entity attesting to information
 *
 * Information about the entity attesting to information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VerificationResultAttestation(
  /**
   * The method by which attested information was submitted/retrieved
   */
  val communicationMethod: CodeableConcept? = null,
  /**
   * The date the information was attested to
   */
  val date: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * When the who is asserting on behalf of another (organization or individual)
   */
  val onBehalfOf: Reference? = null,
  /**
   * A digital identity certificate associated with the proxy entity submitting attested information
   * on behalf of the attestation source
   */
  val proxyIdentityCertificate: String? = null,
  /**
   * Proxy signature
   */
  val proxySignature: Signature? = null,
  /**
   * A digital identity certificate associated with the attestation source
   */
  val sourceIdentityCertificate: String? = null,
  /**
   * Attester signature
   */
  val sourceSignature: Signature? = null,
  /**
   * The individual or organization attesting to information
   */
  val who: Reference? = null
) : BackboneElement

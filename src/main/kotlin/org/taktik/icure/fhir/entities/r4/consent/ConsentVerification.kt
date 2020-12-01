//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.consent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Consent Verified by patient or family
 *
 * Whether a treatment instruction (e.g. artificial respiration yes or no) was verified with the
 * patient, his/her family or another authorized person.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ConsentVerification(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * When consent verified
   */
  val verificationDate: String? = null,
  /**
   * Has been verified
   */
  val verified: Boolean? = null,
  /**
   * Person who verified
   */
  val verifiedWith: Reference? = null
) : BackboneElement

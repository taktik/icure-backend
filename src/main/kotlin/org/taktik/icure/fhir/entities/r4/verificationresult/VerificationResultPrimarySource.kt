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

/**
 * Information about the primary source(s) involved in validation
 *
 * Information about the primary source(s) involved in validation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VerificationResultPrimarySource(
  /**
   * yes | no | undetermined
   */
  val canPushUpdates: CodeableConcept? = null,
  val communicationMethod: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val pushTypeAvailable: List<CodeableConcept> = listOf(),
  val type: List<CodeableConcept> = listOf(),
  /**
   * When the target was validated against the primary source
   */
  val validationDate: String? = null,
  /**
   * successful | failed | unknown
   */
  val validationStatus: CodeableConcept? = null,
  /**
   * Reference to the primary source
   */
  val who: Reference? = null
) : BackboneElement

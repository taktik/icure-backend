//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Any restrictions on medication substitution
 *
 * Indicates whether or not substitution can or should be part of the dispense. In some cases,
 * substitution must happen, in other cases substitution must not happen. This block explains the
 * prescriber's intent. If nothing is specified substitution may be done.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationRequestSubstitution(
  /**
   * Whether substitution is allowed or not
   */
  val allowedBoolean: Boolean? = null,
  /**
   * Whether substitution is allowed or not
   */
  val allowedCodeableConcept: CodeableConcept,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Why should (not) substitution be made
   */
  val reason: CodeableConcept? = null
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.operationoutcome

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * A single issue associated with the action
 *
 * An error, warning, or information message that results from a system action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class OperationOutcomeIssue(
  /**
   * Error or warning code
   */
  val code: String? = null,
  /**
   * Additional details about the error
   */
  val details: CodeableConcept? = null,
  /**
   * Additional diagnostic information about the issue
   */
  val diagnostics: String? = null,
  val expression: List<String> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val location: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * fatal | error | warning | information
   */
  val severity: String? = null
) : BackboneElement

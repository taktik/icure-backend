//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.explanationofbenefit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Note concerning adjudication
 *
 * A note that describes or explains adjudication results in a human readable form.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefitProcessNote(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Language of the text
   */
  val language: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Note instance identifier
   */
  val number: Int? = null,
  /**
   * Note explanatory text
   */
  val text: String? = null,
  /**
   * display | print | printoper
   */
  val type: String? = null
) : BackboneElement

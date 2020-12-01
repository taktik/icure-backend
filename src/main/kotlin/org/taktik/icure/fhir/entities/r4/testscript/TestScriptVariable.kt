//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.testscript

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Placeholder for evaluated elements
 *
 * Variable is set based either on element value in response body or on header field value in the
 * response headers.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TestScriptVariable(
  /**
   * Default, hard-coded, or user-defined value for this variable
   */
  val defaultValue: String? = null,
  /**
   * Natural language description of the variable
   */
  val description: String? = null,
  /**
   * The FHIRPath expression against the fixture body
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * HTTP header field name for source
   */
  val headerField: String? = null,
  /**
   * Hint help text for default value to enter
   */
  val hint: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Descriptive name for this variable
   */
  val name: String? = null,
  /**
   * XPath or JSONPath against the fixture body
   */
  val path: String? = null,
  /**
   * Fixture Id of source expression or headerField within this variable
   */
  val sourceId: String? = null
) : BackboneElement

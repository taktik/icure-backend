//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.expression

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * An expression that can be used to generate a value
 *
 * A expression that is evaluated in a specified context and returns a value. The context of use of
 * the expression must specify the context in which the expression is evaluated, and how the result of
 * the expression is used.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Expression(
  /**
   * Natural language description of the condition
   */
  val description: String? = null,
  /**
   * Expression in specified language
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * text/cql | text/fhirpath | application/x-fhir-query | etc.
   */
  val language: String? = null,
  /**
   * Short name assigned to expression for reuse
   */
  val name: String? = null,
  /**
   * Where the expression is found
   */
  val reference: String? = null
) : Element

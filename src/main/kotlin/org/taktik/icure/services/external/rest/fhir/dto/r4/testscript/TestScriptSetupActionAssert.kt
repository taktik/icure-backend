//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.testscript

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The assertion to perform
 *
 * Evaluates the results of previous operations to determine if the server under test behaves
 * appropriately.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TestScriptSetupActionAssert(
  /**
   * The FHIRPath expression to evaluate against the source fixture
   */
  val compareToSourceExpression: String? = null,
  /**
   * Id of the source fixture to be evaluated
   */
  val compareToSourceId: String? = null,
  /**
   * XPath or JSONPath expression to evaluate against the source fixture
   */
  val compareToSourcePath: String? = null,
  /**
   * Mime type to compare against the 'Content-Type' header
   */
  val contentType: String? = null,
  /**
   * Tracking/reporting assertion description
   */
  val description: String? = null,
  /**
   * response | request
   */
  val direction: String? = null,
  /**
   * The FHIRPath expression to be evaluated
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * HTTP header field name
   */
  val headerField: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Tracking/logging assertion label
   */
  val label: String? = null,
  /**
   * Fixture Id of minimum content resource
   */
  val minimumId: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Perform validation on navigation links?
   */
  val navigationLinks: Boolean? = null,
  /**
   * equals | notEquals | in | notIn | greaterThan | lessThan | empty | notEmpty | contains |
   * notContains | eval
   */
  val operator: String? = null,
  /**
   * XPath or JSONPath expression
   */
  val path: String? = null,
  /**
   * delete | get | options | patch | post | put | head
   */
  val requestMethod: String? = null,
  /**
   * Request URL comparison value
   */
  val requestURL: String? = null,
  /**
   * Resource type
   */
  val resource: String? = null,
  /**
   * okay | created | noContent | notModified | bad | forbidden | notFound | methodNotAllowed |
   * conflict | gone | preconditionFailed | unprocessable
   */
  val response: String? = null,
  /**
   * HTTP response code to test
   */
  val responseCode: String? = null,
  /**
   * Fixture Id of source expression or headerField
   */
  val sourceId: String? = null,
  /**
   * Profile Id of validation profile reference
   */
  val validateProfileId: String? = null,
  /**
   * The value to compare to
   */
  val value: String? = null,
  /**
   * Will this assert produce a warning only on error?
   */
  val warningOnly: Boolean? = null
) : BackboneElement

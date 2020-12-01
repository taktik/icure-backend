//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.usagecontext

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Describes the context of use for a conformance or knowledge resource
 *
 * Specifies clinical/business/etc. metadata that can be used to retrieve, index and/or categorize
 * an artifact. This metadata can either be specific to the applicable population (e.g., age category,
 * DRG) or the specific context of care (e.g., venue, care setting, provider of care).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class UsageContext(
  /**
   * Type of context being specified
   */
  val code: Coding,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Value that defines the context
   */
  val valueCodeableConcept: CodeableConcept,
  /**
   * Value that defines the context
   */
  val valueQuantity: Quantity,
  /**
   * Value that defines the context
   */
  val valueRange: Range,
  /**
   * Value that defines the context
   */
  val valueReference: Reference
) : Element

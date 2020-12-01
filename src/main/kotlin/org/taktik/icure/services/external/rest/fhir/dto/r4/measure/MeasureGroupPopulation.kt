//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.measure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.expression.Expression
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Population criteria
 *
 * A population criteria for the measure.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MeasureGroupPopulation(
  /**
   * initial-population | numerator | numerator-exclusion | denominator | denominator-exclusion |
   * denominator-exception | measure-population | measure-population-exclusion | measure-observation
   */
  val code: CodeableConcept? = null,
  /**
   * The criteria that defines this population
   */
  val criteria: Expression,
  /**
   * The human readable description of this population criteria
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

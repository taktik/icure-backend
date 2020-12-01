//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.measure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.expression.Expression
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Stratifier criteria component for the measure
 *
 * A component of the stratifier criteria for the measure report, specified as either the name of a
 * valid CQL expression defined within a referenced library or a valid FHIR Resource Path.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MeasureGroupStratifierComponent(
  /**
   * Meaning of the stratifier component
   */
  val code: CodeableConcept? = null,
  /**
   * Component of how the measure should be stratified
   */
  val criteria: Expression,
  /**
   * The human readable description of this stratifier component
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.measurereport

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Stratum results, one for each unique value, or set of values, in the stratifier, or stratifier
 * components
 *
 * This element contains the results for a single stratum within the stratifier. For example, when
 * stratifying on administrative gender, there will be four strata, one for each possible gender value.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MeasureReportGroupStratifierStratum(
  val component: List<MeasureReportGroupStratifierStratumComponent> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * What score this stratum achieved
   */
  val measureScore: Quantity? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val population: List<MeasureReportGroupStratifierStratumPopulation> = listOf(),
  /**
   * The stratum value, e.g. male
   */
  val value: CodeableConcept? = null
) : BackboneElement

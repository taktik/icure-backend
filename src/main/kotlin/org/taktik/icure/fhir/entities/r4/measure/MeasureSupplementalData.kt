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
 * What other data should be reported with the measure
 *
 * The supplemental data criteria for the measure report, specified as either the name of a valid
 * CQL expression within a referenced library, or a valid FHIR Resource Path.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MeasureSupplementalData(
  /**
   * Meaning of the supplemental data
   */
  val code: CodeableConcept? = null,
  /**
   * Expression describing additional data to be reported
   */
  val criteria: Expression,
  /**
   * The human readable description of this supplemental data
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val usage: List<CodeableConcept> = listOf()
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.measurereport

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Results of a measure evaluation
 *
 * The MeasureReport resource contains the results of the calculation of a measure; and optionally a
 * reference to the resources involved in that calculation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MeasureReport(
  override val contained: List<Resource> = listOf(),
  /**
   * When the report was generated
   */
  val date: String? = null,
  val evaluatedResource: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  val group: List<MeasureReportGroup> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * increase | decrease
   */
  val improvementNotation: CodeableConcept? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * What measure was calculated
   */
  val measure: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * What period the report covers
   */
  val period: Period,
  /**
   * Who is reporting the data
   */
  val reporter: Reference? = null,
  /**
   * complete | pending | error
   */
  val status: String? = null,
  /**
   * What individual(s) the report is for
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * individual | subject-list | summary | data-collection
   */
  val type: String? = null
) : DomainResource

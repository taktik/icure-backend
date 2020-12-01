//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.researchelementdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.datarequirement.DataRequirement
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.expression.Expression
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing
import org.taktik.icure.services.external.rest.fhir.dto.r4.usagecontext.UsageContext

/**
 * What defines the members of the research element
 *
 * A characteristic that defines the members of the research element. Multiple characteristics are
 * applied with "and" semantics.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ResearchElementDefinitionCharacteristic(
  /**
   * What code or expression defines members?
   */
  val definitionCanonical: String? = null,
  /**
   * What code or expression defines members?
   */
  val definitionCodeableConcept: CodeableConcept,
  /**
   * What code or expression defines members?
   */
  val definitionDataRequirement: DataRequirement,
  /**
   * What code or expression defines members?
   */
  val definitionExpression: Expression,
  /**
   * Whether the characteristic includes or excludes members
   */
  val exclude: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * What time period do participants cover
   */
  val participantEffectiveDateTime: String? = null,
  /**
   * What time period do participants cover
   */
  val participantEffectiveDescription: String? = null,
  /**
   * What time period do participants cover
   */
  val participantEffectiveDuration: Duration? = null,
  /**
   * mean | median | mean-of-mean | mean-of-median | median-of-mean | median-of-median
   */
  val participantEffectiveGroupMeasure: String? = null,
  /**
   * What time period do participants cover
   */
  val participantEffectivePeriod: Period? = null,
  /**
   * Observation time from study start
   */
  val participantEffectiveTimeFromStart: Duration? = null,
  /**
   * What time period do participants cover
   */
  val participantEffectiveTiming: Timing? = null,
  /**
   * What time period does the study cover
   */
  val studyEffectiveDateTime: String? = null,
  /**
   * What time period does the study cover
   */
  val studyEffectiveDescription: String? = null,
  /**
   * What time period does the study cover
   */
  val studyEffectiveDuration: Duration? = null,
  /**
   * mean | median | mean-of-mean | mean-of-median | median-of-mean | median-of-median
   */
  val studyEffectiveGroupMeasure: String? = null,
  /**
   * What time period does the study cover
   */
  val studyEffectivePeriod: Period? = null,
  /**
   * Observation time from study start
   */
  val studyEffectiveTimeFromStart: Duration? = null,
  /**
   * What time period does the study cover
   */
  val studyEffectiveTiming: Timing? = null,
  /**
   * What unit is the outcome described in?
   */
  val unitOfMeasure: CodeableConcept? = null,
  val usageContext: List<UsageContext> = listOf()
) : BackboneElement

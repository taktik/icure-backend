//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.evidencevariable

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
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing
import org.taktik.icure.services.external.rest.fhir.dto.r4.triggerdefinition.TriggerDefinition
import org.taktik.icure.services.external.rest.fhir.dto.r4.usagecontext.UsageContext

/**
 * What defines the members of the evidence element
 *
 * A characteristic that defines the members of the evidence element. Multiple characteristics are
 * applied with "and" semantics.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EvidenceVariableCharacteristic(
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
   * What code or expression defines members?
   */
  val definitionReference: Reference,
  /**
   * What code or expression defines members?
   */
  val definitionTriggerDefinition: TriggerDefinition,
  /**
   * Natural language description of the characteristic
   */
  val description: String? = null,
  /**
   * Whether the characteristic includes or excludes members
   */
  val exclude: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * mean | median | mean-of-mean | mean-of-median | median-of-mean | median-of-median
   */
  val groupMeasure: String? = null,
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
  val participantEffectiveDuration: Duration? = null,
  /**
   * What time period do participants cover
   */
  val participantEffectivePeriod: Period? = null,
  /**
   * What time period do participants cover
   */
  val participantEffectiveTiming: Timing? = null,
  /**
   * Observation time from study start
   */
  val timeFromStart: Duration? = null,
  val usageContext: List<UsageContext> = listOf()
) : BackboneElement

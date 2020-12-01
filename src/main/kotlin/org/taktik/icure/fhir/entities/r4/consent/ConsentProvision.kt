//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.consent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period

/**
 * Constraints to the base Consent.policyRule
 *
 * An exception to the base policy of this consent. An exception can be an addition or removal of
 * access permissions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ConsentProvision(
  val action: List<CodeableConcept> = listOf(),
  val actor: List<ConsentProvisionActor> = listOf(),
  @JsonProperty("class")
  val class_fhir: List<Coding> = listOf(),
  val code: List<CodeableConcept> = listOf(),
  val data: List<ConsentProvisionData> = listOf(),
  /**
   * Timeframe for data controlled by this rule
   */
  val dataPeriod: Period? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Timeframe for this rule
   */
  val period: Period? = null,
  val provision: List<ConsentProvision> = listOf(),
  val purpose: List<Coding> = listOf(),
  val securityLabel: List<Coding> = listOf(),
  /**
   * deny | permit
   */
  val type: String? = null
) : BackboneElement

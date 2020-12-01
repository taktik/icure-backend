//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.familymemberhistory

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.age.Age
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * Condition that the related person had
 *
 * The significant Conditions (or condition) that the family member had. This is a repeating section
 * to allow a system to represent more than one condition per resource, though there is nothing
 * stopping multiple resources - one per condition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class FamilyMemberHistoryCondition(
  /**
   * Condition suffered by relation
   */
  val code: CodeableConcept,
  /**
   * Whether the condition contributed to the cause of death
   */
  val contributedToDeath: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * When condition first manifested
   */
  val onsetAge: Age? = null,
  /**
   * When condition first manifested
   */
  val onsetPeriod: Period? = null,
  /**
   * When condition first manifested
   */
  val onsetRange: Range? = null,
  /**
   * When condition first manifested
   */
  val onsetString: String? = null,
  /**
   * deceased | permanent disability | etc.
   */
  val outcome: CodeableConcept? = null
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.careplan

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Action to occur as part of plan
 *
 * Identifies a planned action to occur as part of the plan.  For example, a medication to be used,
 * lab tests to perform, self-monitoring, education, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CarePlanActivity(
  /**
   * In-line definition of activity
   */
  val detail: CarePlanActivityDetail? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val outcomeCodeableConcept: List<CodeableConcept> = listOf(),
  val outcomeReference: List<Reference> = listOf(),
  val progress: List<Annotation> = listOf(),
  /**
   * Activity details defined in specific resource
   */
  val reference: Reference? = null
) : BackboneElement

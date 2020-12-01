//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.adverseevent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Information on the possible cause of the event
 *
 * Information on the possible cause of the event.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AdverseEventSuspectEntityCausality(
  /**
   * Assessment of if the entity caused the event
   */
  val assessment: CodeableConcept? = null,
  /**
   * AdverseEvent.suspectEntity.causalityAuthor
   */
  val author: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * ProbabilityScale | Bayesian | Checklist
   */
  val method: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * AdverseEvent.suspectEntity.causalityProductRelatedness
   */
  val productRelatedness: String? = null
) : BackboneElement

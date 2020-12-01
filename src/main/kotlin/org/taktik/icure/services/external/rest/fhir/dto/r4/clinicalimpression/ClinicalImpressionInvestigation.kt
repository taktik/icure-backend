//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.clinicalimpression

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * One or more sets of investigations (signs, symptoms, etc.)
 *
 * One or more sets of investigations (signs, symptoms, etc.). The actual grouping of investigations
 * varies greatly depending on the type and context of the assessment. These investigations may include
 * data generated during the assessment process, or data previously generated and recorded that is
 * pertinent to the outcomes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClinicalImpressionInvestigation(
  /**
   * A name/code for the set
   */
  val code: CodeableConcept,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val item: List<Reference> = listOf(),
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationdispense

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Whether a substitution was performed on the dispense
 *
 * Indicates whether or not substitution was made as part of the dispense.  In some cases,
 * substitution will be expected but does not happen, in other cases substitution is not expected but
 * does happen.  This block explains what substitution did or did not happen and why.  If nothing is
 * specified, substitution was not done.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationDispenseSubstitution(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val reason: List<CodeableConcept> = listOf(),
  val responsibleParty: List<Reference> = listOf(),
  /**
   * Code signifying whether a different drug was dispensed from what was prescribed
   */
  val type: CodeableConcept? = null,
  /**
   * Whether a substitution was or was not performed on the dispense
   */
  val wasSubstituted: Boolean? = null
) : BackboneElement

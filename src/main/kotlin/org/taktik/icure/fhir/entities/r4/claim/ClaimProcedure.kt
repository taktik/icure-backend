//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.claim

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Clinical procedures performed
 *
 * Procedures performed on the patient relevant to the billing items with the claim.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimProcedure(
  /**
   * When the procedure was performed
   */
  val date: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Specific clinical procedure
   */
  val procedureCodeableConcept: CodeableConcept,
  /**
   * Specific clinical procedure
   */
  val procedureReference: Reference,
  /**
   * Procedure instance identifier
   */
  val sequence: Int? = null,
  val type: List<CodeableConcept> = listOf(),
  val udi: List<Reference> = listOf()
) : BackboneElement

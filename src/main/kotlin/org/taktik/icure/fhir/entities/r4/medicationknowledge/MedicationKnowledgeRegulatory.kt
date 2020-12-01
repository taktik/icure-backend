//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicationknowledge

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Regulatory information about a medication
 *
 * Regulatory information about a medication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationKnowledgeRegulatory(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The maximum number of units of the medication that can be dispensed in a period
   */
  val maxDispense: MedicationKnowledgeRegulatoryMaxDispense? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Specifies the authority of the regulation
   */
  val regulatoryAuthority: Reference,
  val schedule: List<MedicationKnowledgeRegulatorySchedule> = listOf(),
  val substitution: List<MedicationKnowledgeRegulatorySubstitution> = listOf()
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.documentreference

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Clinical context of document
 *
 * The clinical context in which the document was prepared.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DocumentReferenceContext(
  val encounter: List<Reference> = listOf(),
  val event: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Kind of facility where patient was seen
   */
  val facilityType: CodeableConcept? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Time of service that is being documented
   */
  val period: Period? = null,
  /**
   * Additional details about where the content was created (e.g. clinical specialty)
   */
  val practiceSetting: CodeableConcept? = null,
  val related: List<Reference> = listOf(),
  /**
   * Patient demographics from source
   */
  val sourcePatientInfo: Reference? = null
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproductindication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.population.Population
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * MedicinalProductIndication
 *
 * Indication for the Medicinal Product.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductIndication(
  val comorbidity: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * The status of the disease or symptom for which the indication applies
   */
  val diseaseStatus: CodeableConcept? = null,
  /**
   * The disease, symptom or procedure that is the indication for treatment
   */
  val diseaseSymptomProcedure: CodeableConcept? = null,
  /**
   * Timing or duration information as part of the indication
   */
  val duration: Quantity? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * The intended effect, aim or strategy to be achieved by the indication
   */
  val intendedEffect: CodeableConcept? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val otherTherapy: List<MedicinalProductIndicationOtherTherapy> = listOf(),
  val population: List<Population> = listOf(),
  val subject: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val undesirableEffect: List<Reference> = listOf()
) : DomainResource

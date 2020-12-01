//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicinalproductcontraindication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.population.Population
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * MedicinalProductContraindication
 *
 * The clinical particulars - indications, contraindications etc. of a medicinal product, including
 * for regulatory purposes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductContraindication(
  val comorbidity: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * The disease, symptom or procedure for the contraindication
   */
  val disease: CodeableConcept? = null,
  /**
   * The status of the disease or symptom for the contraindication
   */
  val diseaseStatus: CodeableConcept? = null,
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
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val otherTherapy: List<MedicinalProductContraindicationOtherTherapy> = listOf(),
  val population: List<Population> = listOf(),
  val subject: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val therapeuticIndication: List<Reference> = listOf()
) : DomainResource

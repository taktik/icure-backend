//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.observationdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Definition of an observation
 *
 * Set of definitional characteristics for a kind of observation or measurement produced or consumed
 * by an orderable health care service.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ObservationDefinition(
  /**
   * Value set of abnormal coded values for the observations conforming to this
   * ObservationDefinition
   */
  val abnormalCodedValueSet: Reference? = null,
  val category: List<CodeableConcept> = listOf(),
  /**
   * Type of observation (code / type)
   */
  val code: CodeableConcept,
  override val contained: List<Resource> = listOf(),
  /**
   * Value set of critical coded values for the observations conforming to this
   * ObservationDefinition
   */
  val criticalCodedValueSet: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
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
  /**
   * Method used to produce the observation
   */
  val method: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Multiple results allowed
   */
  val multipleResultsAllowed: Boolean? = null,
  /**
   * Value set of normal coded values for the observations conforming to this ObservationDefinition
   */
  val normalCodedValueSet: Reference? = null,
  val permittedDataType: List<String> = listOf(),
  /**
   * Preferred report name
   */
  val preferredReportName: String? = null,
  val qualifiedInterval: List<ObservationDefinitionQualifiedInterval> = listOf(),
  /**
   * Characteristics of quantitative results
   */
  val quantitativeDetails: ObservationDefinitionQuantitativeDetails? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Value set of valid coded values for the observations conforming to this ObservationDefinition
   */
  val validCodedValueSet: Reference? = null
) : DomainResource

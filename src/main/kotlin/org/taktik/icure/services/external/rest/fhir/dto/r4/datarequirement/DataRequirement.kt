//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.datarequirement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Describes a required data item
 *
 * Describes a required data item for evaluation in terms of the type of data, and optional code or
 * date-based filters of the data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DataRequirement(
  val codeFilter: List<DataRequirementCodeFilter> = listOf(),
  val dateFilter: List<DataRequirementDateFilter> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Number of results
   */
  val limit: Int? = null,
  val mustSupport: List<String> = listOf(),
  val profile: List<String> = listOf(),
  val sort: List<DataRequirementSort> = listOf(),
  /**
   * E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
   */
  val subjectCodeableConcept: CodeableConcept? = null,
  /**
   * E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
   */
  val subjectReference: Reference? = null,
  /**
   * The type of the required data
   */
  val type: String? = null
) : Element

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.population

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * A definition of a set of people that apply to some clinically related context, for example people
 * contraindicated for a certain medication
 *
 * A populatioof people with some set of grouping criteria.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Population(
  /**
   * The age of the specific population
   */
  val ageCodeableConcept: CodeableConcept? = null,
  /**
   * The age of the specific population
   */
  val ageRange: Range? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * The gender of the specific population
   */
  val gender: CodeableConcept? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The existing physiological conditions of the specific population to which this applies
   */
  val physiologicalCondition: CodeableConcept? = null,
  /**
   * Race of the specific population
   */
  val race: CodeableConcept? = null
) : BackboneElement

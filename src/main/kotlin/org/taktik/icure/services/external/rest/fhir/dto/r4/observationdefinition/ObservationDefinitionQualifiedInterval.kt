//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.observationdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * Qualified range for continuous and ordinal observation results
 *
 * Multiple  ranges of results qualified by different contexts for ordinal or continuous
 * observations conforming to this ObservationDefinition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ObservationDefinitionQualifiedInterval(
  /**
   * Applicable age range, if relevant
   */
  val age: Range? = null,
  val appliesTo: List<CodeableConcept> = listOf(),
  /**
   * reference | critical | absolute
   */
  val category: String? = null,
  /**
   * Condition associated with the reference range
   */
  val condition: String? = null,
  /**
   * Range context qualifier
   */
  val context: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * male | female | other | unknown
   */
  val gender: String? = null,
  /**
   * Applicable gestational age range, if relevant
   */
  val gestationalAge: Range? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The interval itself, for continuous or ordinal observations
   */
  val range: Range? = null
) : BackboneElement

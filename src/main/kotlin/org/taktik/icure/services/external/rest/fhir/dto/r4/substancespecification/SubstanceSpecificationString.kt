//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancespecification

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Codes associated with the substance
 *
 * Codes associated with the substance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSpecificationString(
  /**
   * The specific code
   */
  val code: CodeableConcept? = null,
  /**
   * Any comment can be provided in this field, if necessary
   */
  val comment: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val source: List<Reference> = listOf(),
  /**
   * Status of the code assignment
   */
  val status: CodeableConcept? = null,
  /**
   * The date at which the code status is changed as part of the terminology maintenance
   */
  val statusDate: String? = null
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.contract

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Contract precursor content
 *
 * Precusory content developed with a focus and intent of supporting the formation a Contract
 * instance, which may be associated with and transformable into a Contract.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ContractContentDefinition(
  /**
   * Publication Ownership
   */
  val copyright: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * When published
   */
  val publicationDate: String? = null,
  /**
   * amended | appended | cancelled | disputed | entered-in-error | executable | executed |
   * negotiable | offered | policy | rejected | renewed | revoked | resolved | terminated
   */
  val publicationStatus: String? = null,
  /**
   * Publisher Entity
   */
  val publisher: Reference? = null,
  /**
   * Detailed Content Type Definition
   */
  val subType: CodeableConcept? = null,
  /**
   * Content structure and use
   */
  val type: CodeableConcept
) : BackboneElement

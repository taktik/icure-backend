//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.immunization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Educational material presented to patient
 *
 * Educational material presented to the patient (or guardian) at the time of vaccine
 * administration.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImmunizationEducation(
  /**
   * Educational material document identifier
   */
  val documentType: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Educational material presentation date
   */
  val presentationDate: String? = null,
  /**
   * Educational material publication date
   */
  val publicationDate: String? = null,
  /**
   * Educational material reference pointer
   */
  val reference: String? = null
) : BackboneElement

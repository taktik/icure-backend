//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.practitioner

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Certification, licenses, or training pertaining to the provision of care
 *
 * The official certifications, training, and licenses that authorize or otherwise pertain to the
 * provision of care by the practitioner.  For example, a medical license issued by a medical board
 * authorizing the practitioner to practice medicine within a certian locality.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PractitionerQualification(
  /**
   * Coded representation of the qualification
   */
  val code: CodeableConcept,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * Organization that regulates and issues the qualification
   */
  val issuer: Reference? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Period during which the qualification is valid
   */
  val period: Period? = null
) : BackboneElement

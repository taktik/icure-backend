//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.identifier

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * An identifier intended for computation
 *
 * An identifier - identifies some entity uniquely and unambiguously. Typically this is used for
 * business identifiers.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Identifier(
  /**
   * Organization that issued id (may be just text)
   */
  val assigner: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Time period when id is/was valid for use
   */
  val period: Period? = null,
  /**
   * The namespace for the identifier value
   */
  val system: String? = null,
  /**
   * Description of identifier
   */
  val type: CodeableConcept? = null,
  /**
   * usual | official | temp | secondary | old (If known)
   */
  val use: String? = null,
  /**
   * The value that is unique
   */
  val value: String? = null
) : Element

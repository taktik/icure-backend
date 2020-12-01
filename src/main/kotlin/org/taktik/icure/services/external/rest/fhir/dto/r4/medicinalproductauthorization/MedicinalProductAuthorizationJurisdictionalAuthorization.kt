//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproductauthorization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period

/**
 * Authorization in areas within a country
 *
 * Authorization in areas within a country.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductAuthorizationJurisdictionalAuthorization(
  /**
   * Country of authorization
   */
  val country: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * The legal status of supply in a jurisdiction or region
   */
  val legalStatusOfSupply: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The start and expected end date of the authorization
   */
  val validityPeriod: Period? = null
) : BackboneElement

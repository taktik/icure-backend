//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.group

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Include / Exclude group members by Trait
 *
 * Identifies traits whose presence r absence is shared by members of the group.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class GroupCharacteristic(
  /**
   * Kind of characteristic
   */
  val code: CodeableConcept,
  /**
   * Group includes or excludes
   */
  val exclude: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Period over which characteristic is tested
   */
  val period: Period? = null,
  /**
   * Value held by characteristic
   */
  val valueBoolean: Boolean? = null,
  /**
   * Value held by characteristic
   */
  val valueCodeableConcept: CodeableConcept,
  /**
   * Value held by characteristic
   */
  val valueQuantity: Quantity,
  /**
   * Value held by characteristic
   */
  val valueRange: Range,
  /**
   * Value held by characteristic
   */
  val valueReference: Reference
) : BackboneElement

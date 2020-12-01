//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.encounter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * List of locations where the patient has been
 *
 * List of locations where  the patient has been during this encounter.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EncounterLocation(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Location the encounter takes place
   */
  val location: Reference,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Time period during which the patient was present at the location
   */
  val period: Period? = null,
  /**
   * The physical type of the location (usually the level in the location hierachy - bed room ward
   * etc.)
   */
  val physicalType: CodeableConcept? = null,
  /**
   * planned | active | reserved | completed
   */
  val status: String? = null
) : BackboneElement

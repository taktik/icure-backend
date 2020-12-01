//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.encounter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period

/**
 * List of past encounter classes
 *
 * The class history permits the tracking of the encounters transitions without needing to go
 * through the resource history.  This would be used for a case where an admission starts of as an
 * emergency encounter, then transitions into an inpatient scenario. Doing this and not restarting a
 * new encounter ensures that any lab/diagnostic results can more easily follow the patient and not
 * require re-processing and not get lost or cancelled during a kind of discharge from emergency to
 * inpatient.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EncounterClassHistory(
  /**
   * inpatient | outpatient | ambulatory | emergency +
   */
  @JsonProperty("class")
  val class_fhir: Coding,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The time that the episode was in the specified class
   */
  val period: Period
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationknowledge

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The time course of drug absorption, distribution, metabolism and excretion of a medication from
 * the body
 *
 * The time course of drug absorption, distribution, metabolism and excretion of a medication from
 * the body.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationKnowledgeKinetics(
  val areaUnderCurve: List<Quantity> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Time required for concentration in the body to decrease by half
   */
  val halfLifePeriod: Duration? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val lethalDose50: List<Quantity> = listOf(),
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.researchsubject

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Physical entity which is the primary unit of interest in the study
 *
 * A physical entity which is the primary unit of operational and/or administrative interest in a
 * study.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ResearchSubject(
  /**
   * What path was followed
   */
  val actualArm: String? = null,
  /**
   * What path should be followed
   */
  val assignedArm: String? = null,
  /**
   * Agreement to participate in study
   */
  val consent: Reference? = null,
  override val contained: List<Resource> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Who is part of study
   */
  val individual: Reference,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Start and end of participation
   */
  val period: Period? = null,
  /**
   * candidate | eligible | follow-up | ineligible | not-registered | off-study | on-study |
   * on-study-intervention | on-study-observation | pending-on-study | potential-candidate | screening
   * | withdrawn
   */
  val status: String? = null,
  /**
   * Study subject is part of
   */
  val study: Reference,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

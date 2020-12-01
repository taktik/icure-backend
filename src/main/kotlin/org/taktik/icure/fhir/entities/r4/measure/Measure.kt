/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.measure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * A quality measure definition
 *
 * The Measure resource provides the definition of a quality measure.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Measure(
  /**
   * When the measure was approved by publisher
   */
  val approvalDate: String? = null,
  val author: List<ContactDetail> = listOf(),
  /**
   * Summary of clinical guidelines
   */
  val clinicalRecommendationStatement: String? = null,
  /**
   * opportunity | all-or-nothing | linear | weighted
   */
  val compositeScoring: CodeableConcept? = null,
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Use and/or publishing restrictions
   */
  val copyright: String? = null,
  /**
   * Date last changed
   */
  val date: String? = null,
  val definition: List<String> = listOf(),
  /**
   * Natural language description of the measure
   */
  val description: String? = null,
  /**
   * Disclaimer for use of the measure or its referenced content
   */
  val disclaimer: String? = null,
  val editor: List<ContactDetail> = listOf(),
  /**
   * When the measure is expected to be used
   */
  val effectivePeriod: Period? = null,
  val endorser: List<ContactDetail> = listOf(),
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  val group: List<MeasureGroup> = listOf(),
  /**
   * Additional guidance for implementers
   */
  val guidance: String? = null,
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
   * increase | decrease
   */
  val improvementNotation: CodeableConcept? = null,
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * When the measure was last reviewed
   */
  val lastReviewDate: String? = null,
  val library: List<String> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this measure (computer friendly)
   */
  val name: String? = null,
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this measure is defined
   */
  val purpose: String? = null,
  /**
   * How is rate aggregation performed for this measure
   */
  val rateAggregation: String? = null,
  /**
   * Detailed description of why the measure exists
   */
  val rationale: String? = null,
  val relatedArtifact: List<RelatedArtifact> = listOf(),
  val reviewer: List<ContactDetail> = listOf(),
  /**
   * How risk adjustment is applied for this measure
   */
  val riskAdjustment: String? = null,
  /**
   * proportion | ratio | continuous-variable | cohort
   */
  val scoring: CodeableConcept? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
   */
  val subjectCodeableConcept: CodeableConcept? = null,
  /**
   * E.g. Patient, Practitioner, RelatedPerson, Organization, Location, Device
   */
  val subjectReference: Reference? = null,
  /**
   * Subordinate title of the measure
   */
  val subtitle: String? = null,
  val supplementalData: List<MeasureSupplementalData> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this measure (human friendly)
   */
  val title: String? = null,
  val topic: List<CodeableConcept> = listOf(),
  val type: List<CodeableConcept> = listOf(),
  /**
   * Canonical identifier for this measure, represented as a URI (globally unique)
   */
  val url: String? = null,
  /**
   * Describes the clinical usage of the measure
   */
  val usage: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the measure
   */
  val version: String? = null
) : DomainResource

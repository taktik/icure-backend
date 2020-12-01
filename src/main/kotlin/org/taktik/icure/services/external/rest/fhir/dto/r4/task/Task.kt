//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.task

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A task to be performed
 *
 * A task to be performed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Task(
  /**
   * Task Creation Date
   */
  val authoredOn: String? = null,
  val basedOn: List<Reference> = listOf(),
  /**
   * E.g. "Specimen collected", "IV prepped"
   */
  val businessStatus: CodeableConcept? = null,
  /**
   * Task Type
   */
  val code: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Human-readable explanation of task
   */
  val description: String? = null,
  /**
   * Healthcare event during which this task originated
   */
  val encounter: Reference? = null,
  /**
   * Start and end time of execution
   */
  val executionPeriod: Period? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * What task is acting on
   */
  val focus: Reference? = null,
  /**
   * Beneficiary of the Task
   */
  @JsonProperty("for")
  val for_fhir: Reference? = null,
  /**
   * Requisition or grouper id
   */
  val groupIdentifier: Identifier? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val input: List<TaskInput> = listOf(),
  /**
   * Formal definition of task
   */
  val instantiatesCanonical: String? = null,
  /**
   * Formal definition of task
   */
  val instantiatesUri: String? = null,
  val insurance: List<Reference> = listOf(),
  /**
   * unknown | proposal | plan | order | original-order | reflex-order | filler-order |
   * instance-order | option
   */
  val intent: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Task Last Modified Date
   */
  val lastModified: String? = null,
  /**
   * Where task occurs
   */
  val location: Reference? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val output: List<TaskOutput> = listOf(),
  /**
   * Responsible individual
   */
  val owner: Reference? = null,
  val partOf: List<Reference> = listOf(),
  val performerType: List<CodeableConcept> = listOf(),
  /**
   * routine | urgent | asap | stat
   */
  val priority: String? = null,
  /**
   * Why task is needed
   */
  val reasonCode: CodeableConcept? = null,
  /**
   * Why task is needed
   */
  val reasonReference: Reference? = null,
  val relevantHistory: List<Reference> = listOf(),
  /**
   * Who is asking for task to be done
   */
  val requester: Reference? = null,
  /**
   * Constraints on fulfillment tasks
   */
  val restriction: TaskRestriction? = null,
  /**
   * draft | requested | received | accepted | +
   */
  val status: String? = null,
  /**
   * Reason for current status
   */
  val statusReason: CodeableConcept? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

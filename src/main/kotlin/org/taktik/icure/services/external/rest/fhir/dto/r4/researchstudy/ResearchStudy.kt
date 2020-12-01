//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.researchstudy

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactdetail.ContactDetail
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.relatedartifact.RelatedArtifact

/**
 * Investigation to increase healthcare-related patient-independent knowledge
 *
 * A process where a researcher or organization plans and then executes a series of steps intended
 * to increase the field of healthcare-related knowledge.  This includes studies of safety, efficacy,
 * comparative effectiveness and other information about medications, devices, therapies and other
 * interventional and investigative techniques.  A ResearchStudy involves the gathering of information
 * about human or animal subjects.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ResearchStudy(
  val arm: List<ResearchStudyArm> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  val condition: List<CodeableConcept> = listOf(),
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * What this is study doing
   */
  val description: String? = null,
  val enrollment: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  val focus: List<CodeableConcept> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val keyword: List<CodeableConcept> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val location: List<CodeableConcept> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val objective: List<ResearchStudyObjective> = listOf(),
  val partOf: List<Reference> = listOf(),
  /**
   * When the study began and ended
   */
  val period: Period? = null,
  /**
   * n-a | early-phase-1 | phase-1 | phase-1-phase-2 | phase-2 | phase-2-phase-3 | phase-3 | phase-4
   */
  val phase: CodeableConcept? = null,
  /**
   * treatment | prevention | diagnostic | supportive-care | screening | health-services-research |
   * basic-science | device-feasibility
   */
  val primaryPurposeType: CodeableConcept? = null,
  /**
   * Researcher who oversees multiple aspects of the study
   */
  val principalInvestigator: Reference? = null,
  val protocol: List<Reference> = listOf(),
  /**
   * accrual-goal-met | closed-due-to-toxicity | closed-due-to-lack-of-study-progress |
   * temporarily-closed-per-study-design
   */
  val reasonStopped: CodeableConcept? = null,
  val relatedArtifact: List<RelatedArtifact> = listOf(),
  val site: List<Reference> = listOf(),
  /**
   * Organization that initiates and is legally responsible for the study
   */
  val sponsor: Reference? = null,
  /**
   * active | administratively-completed | approved | closed-to-accrual |
   * closed-to-accrual-and-intervention | completed | disapproved | in-review |
   * temporarily-closed-to-accrual | temporarily-closed-to-accrual-and-intervention | withdrawn
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this study
   */
  val title: String? = null
) : DomainResource

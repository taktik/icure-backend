//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.diagnosticreport

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.attachment.Attachment
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A Diagnostic report - a combination of request information, atomic results, images,
 * interpretation, as well as formatted reports
 *
 * The findings and interpretation of diagnostic  tests performed on patients, groups of patients,
 * devices, and locations, and/or specimens derived from these. The report includes clinical context
 * such as requesting and provider information, and some mix of atomic results, images, textual and
 * coded interpretations, and formatted representation of diagnostic reports.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DiagnosticReport(
  val basedOn: List<Reference> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  /**
   * Name/Code for this diagnostic report
   */
  val code: CodeableConcept,
  /**
   * Clinical conclusion (interpretation) of test results
   */
  val conclusion: String? = null,
  val conclusionCode: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Clinically relevant time/time-period for report
   */
  val effectiveDateTime: String? = null,
  /**
   * Clinically relevant time/time-period for report
   */
  val effectivePeriod: Period? = null,
  /**
   * Health care event when test ordered
   */
  val encounter: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  val imagingStudy: List<Reference> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * DateTime this version was made
   */
  val issued: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val media: List<DiagnosticReportMedia> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val performer: List<Reference> = listOf(),
  val presentedForm: List<Attachment> = listOf(),
  val result: List<Reference> = listOf(),
  val resultsInterpreter: List<Reference> = listOf(),
  val specimen: List<Reference> = listOf(),
  /**
   * registered | partial | preliminary | final +
   */
  val status: String? = null,
  /**
   * The subject of the report - usually, but not always, the patient
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource

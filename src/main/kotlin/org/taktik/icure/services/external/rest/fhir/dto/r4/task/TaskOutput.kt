//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.task

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.address.Address
import org.taktik.icure.services.external.rest.fhir.dto.r4.age.Age
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.attachment.Attachment
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactdetail.ContactDetail
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint.ContactPoint
import org.taktik.icure.services.external.rest.fhir.dto.r4.contributor.Contributor
import org.taktik.icure.services.external.rest.fhir.dto.r4.count.Count
import org.taktik.icure.services.external.rest.fhir.dto.r4.datarequirement.DataRequirement
import org.taktik.icure.services.external.rest.fhir.dto.r4.distance.Distance
import org.taktik.icure.services.external.rest.fhir.dto.r4.dosage.Dosage
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.expression.Expression
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.humanname.HumanName
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money
import org.taktik.icure.services.external.rest.fhir.dto.r4.parameterdefinition.ParameterDefinition
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.ratio.Ratio
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.services.external.rest.fhir.dto.r4.sampleddata.SampledData
import org.taktik.icure.services.external.rest.fhir.dto.r4.signature.Signature
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing
import org.taktik.icure.services.external.rest.fhir.dto.r4.triggerdefinition.TriggerDefinition
import org.taktik.icure.services.external.rest.fhir.dto.r4.usagecontext.UsageContext

/**
 * Information produced as part of task
 *
 * Outputs produced by the Task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TaskOutput(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Label for output
   */
  val type: CodeableConcept,
  /**
   * Result of output
   */
  val valueAddress: Address,
  /**
   * Result of output
   */
  val valueAge: Age,
  /**
   * Result of output
   */
  val valueAnnotation: Annotation,
  /**
   * Result of output
   */
  val valueAttachment: Attachment,
  /**
   * Result of output
   */
  val valueBase64Binary: String? = null,
  /**
   * Result of output
   */
  val valueBoolean: Boolean? = null,
  /**
   * Result of output
   */
  val valueCanonical: String? = null,
  /**
   * Result of output
   */
  val valueCode: String? = null,
  /**
   * Result of output
   */
  val valueCodeableConcept: CodeableConcept,
  /**
   * Result of output
   */
  val valueCoding: Coding,
  /**
   * Result of output
   */
  val valueContactDetail: ContactDetail,
  /**
   * Result of output
   */
  val valueContactPoint: ContactPoint,
  /**
   * Result of output
   */
  val valueContributor: Contributor,
  /**
   * Result of output
   */
  val valueCount: Count,
  /**
   * Result of output
   */
  val valueDataRequirement: DataRequirement,
  /**
   * Result of output
   */
  val valueDate: String? = null,
  /**
   * Result of output
   */
  val valueDateTime: String? = null,
  /**
   * Result of output
   */
  val valueDecimal: Float? = null,
  /**
   * Result of output
   */
  val valueDistance: Distance,
  /**
   * Result of output
   */
  val valueDosage: Dosage,
  /**
   * Result of output
   */
  val valueDuration: Duration,
  /**
   * Result of output
   */
  val valueExpression: Expression,
  /**
   * Result of output
   */
  val valueHumanName: HumanName,
  /**
   * Result of output
   */
  val valueId: String? = null,
  /**
   * Result of output
   */
  val valueIdentifier: Identifier,
  /**
   * Result of output
   */
  val valueInstant: String? = null,
  /**
   * Result of output
   */
  val valueInteger: Int? = null,
  /**
   * Result of output
   */
  val valueMarkdown: String? = null,
  /**
   * Result of output
   */
  val valueMeta: Meta,
  /**
   * Result of output
   */
  val valueMoney: Money,
  /**
   * Result of output
   */
  val valueOid: String? = null,
  /**
   * Result of output
   */
  val valueParameterDefinition: ParameterDefinition,
  /**
   * Result of output
   */
  val valuePeriod: Period,
  /**
   * Result of output
   */
  val valuePositiveInt: Int? = null,
  /**
   * Result of output
   */
  val valueQuantity: Quantity,
  /**
   * Result of output
   */
  val valueRange: Range,
  /**
   * Result of output
   */
  val valueRatio: Ratio,
  /**
   * Result of output
   */
  val valueReference: Reference,
  /**
   * Result of output
   */
  val valueRelatedArtifact: RelatedArtifact,
  /**
   * Result of output
   */
  val valueSampledData: SampledData,
  /**
   * Result of output
   */
  val valueSignature: Signature,
  /**
   * Result of output
   */
  val valueString: String? = null,
  /**
   * Result of output
   */
  val valueTime: String? = null,
  /**
   * Result of output
   */
  val valueTiming: Timing,
  /**
   * Result of output
   */
  val valueTriggerDefinition: TriggerDefinition,
  /**
   * Result of output
   */
  val valueUnsignedInt: Int? = null,
  /**
   * Result of output
   */
  val valueUri: String? = null,
  /**
   * Result of output
   */
  val valueUrl: String? = null,
  /**
   * Result of output
   */
  val valueUsageContext: UsageContext,
  /**
   * Result of output
   */
  val valueUuid: String? = null
) : BackboneElement

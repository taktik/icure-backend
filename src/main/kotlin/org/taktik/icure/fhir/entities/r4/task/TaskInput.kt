//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.task

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.address.Address
import org.taktik.icure.fhir.entities.r4.age.Age
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.contactpoint.ContactPoint
import org.taktik.icure.fhir.entities.r4.contributor.Contributor
import org.taktik.icure.fhir.entities.r4.count.Count
import org.taktik.icure.fhir.entities.r4.datarequirement.DataRequirement
import org.taktik.icure.fhir.entities.r4.distance.Distance
import org.taktik.icure.fhir.entities.r4.dosage.Dosage
import org.taktik.icure.fhir.entities.r4.duration.Duration
import org.taktik.icure.fhir.entities.r4.expression.Expression
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.humanname.HumanName
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.money.Money
import org.taktik.icure.fhir.entities.r4.parameterdefinition.ParameterDefinition
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.ratio.Ratio
import org.taktik.icure.fhir.entities.r4.reference.Reference
import org.taktik.icure.fhir.entities.r4.relatedartifact.RelatedArtifact
import org.taktik.icure.fhir.entities.r4.sampleddata.SampledData
import org.taktik.icure.fhir.entities.r4.signature.Signature
import org.taktik.icure.fhir.entities.r4.timing.Timing
import org.taktik.icure.fhir.entities.r4.triggerdefinition.TriggerDefinition
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * Information used to perform task
 *
 * Additional information that may be needed in the execution of the task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TaskInput(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Label for the input
   */
  val type: CodeableConcept,
  /**
   * Content to use in performing the task
   */
  val valueAddress: Address,
  /**
   * Content to use in performing the task
   */
  val valueAge: Age,
  /**
   * Content to use in performing the task
   */
  val valueAnnotation: Annotation,
  /**
   * Content to use in performing the task
   */
  val valueAttachment: Attachment,
  /**
   * Content to use in performing the task
   */
  val valueBase64Binary: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueBoolean: Boolean? = null,
  /**
   * Content to use in performing the task
   */
  val valueCanonical: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueCode: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueCodeableConcept: CodeableConcept,
  /**
   * Content to use in performing the task
   */
  val valueCoding: Coding,
  /**
   * Content to use in performing the task
   */
  val valueContactDetail: ContactDetail,
  /**
   * Content to use in performing the task
   */
  val valueContactPoint: ContactPoint,
  /**
   * Content to use in performing the task
   */
  val valueContributor: Contributor,
  /**
   * Content to use in performing the task
   */
  val valueCount: Count,
  /**
   * Content to use in performing the task
   */
  val valueDataRequirement: DataRequirement,
  /**
   * Content to use in performing the task
   */
  val valueDate: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueDateTime: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueDecimal: Float? = null,
  /**
   * Content to use in performing the task
   */
  val valueDistance: Distance,
  /**
   * Content to use in performing the task
   */
  val valueDosage: Dosage,
  /**
   * Content to use in performing the task
   */
  val valueDuration: Duration,
  /**
   * Content to use in performing the task
   */
  val valueExpression: Expression,
  /**
   * Content to use in performing the task
   */
  val valueHumanName: HumanName,
  /**
   * Content to use in performing the task
   */
  val valueId: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueIdentifier: Identifier,
  /**
   * Content to use in performing the task
   */
  val valueInstant: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueInteger: Int? = null,
  /**
   * Content to use in performing the task
   */
  val valueMarkdown: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueMeta: Meta,
  /**
   * Content to use in performing the task
   */
  val valueMoney: Money,
  /**
   * Content to use in performing the task
   */
  val valueOid: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueParameterDefinition: ParameterDefinition,
  /**
   * Content to use in performing the task
   */
  val valuePeriod: Period,
  /**
   * Content to use in performing the task
   */
  val valuePositiveInt: Int? = null,
  /**
   * Content to use in performing the task
   */
  val valueQuantity: Quantity,
  /**
   * Content to use in performing the task
   */
  val valueRange: Range,
  /**
   * Content to use in performing the task
   */
  val valueRatio: Ratio,
  /**
   * Content to use in performing the task
   */
  val valueReference: Reference,
  /**
   * Content to use in performing the task
   */
  val valueRelatedArtifact: RelatedArtifact,
  /**
   * Content to use in performing the task
   */
  val valueSampledData: SampledData,
  /**
   * Content to use in performing the task
   */
  val valueSignature: Signature,
  /**
   * Content to use in performing the task
   */
  val valueString: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueTime: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueTiming: Timing,
  /**
   * Content to use in performing the task
   */
  val valueTriggerDefinition: TriggerDefinition,
  /**
   * Content to use in performing the task
   */
  val valueUnsignedInt: Int? = null,
  /**
   * Content to use in performing the task
   */
  val valueUri: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueUrl: String? = null,
  /**
   * Content to use in performing the task
   */
  val valueUsageContext: UsageContext,
  /**
   * Content to use in performing the task
   */
  val valueUuid: String? = null
) : BackboneElement

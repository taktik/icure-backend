//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.extension

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.address.Address
import org.taktik.icure.services.external.rest.fhir.dto.r4.age.Age
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.attachment.Attachment
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
 * Optional Extensions Element
 *
 * Optional Extension Element - found in all resources.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Extension(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * identifies the meaning of the extension
   */
  val url: String? = null,
  /**
   * Value of extension
   */
  val valueAddress: Address? = null,
  /**
   * Value of extension
   */
  val valueAge: Age? = null,
  /**
   * Value of extension
   */
  val valueAnnotation: Annotation? = null,
  /**
   * Value of extension
   */
  val valueAttachment: Attachment? = null,
  /**
   * Value of extension
   */
  val valueBase64Binary: String? = null,
  /**
   * Value of extension
   */
  val valueBoolean: Boolean? = null,
  /**
   * Value of extension
   */
  val valueCanonical: String? = null,
  /**
   * Value of extension
   */
  val valueCode: String? = null,
  /**
   * Value of extension
   */
  val valueCodeableConcept: CodeableConcept? = null,
  /**
   * Value of extension
   */
  val valueCoding: Coding? = null,
  /**
   * Value of extension
   */
  val valueContactDetail: ContactDetail? = null,
  /**
   * Value of extension
   */
  val valueContactPoint: ContactPoint? = null,
  /**
   * Value of extension
   */
  val valueContributor: Contributor? = null,
  /**
   * Value of extension
   */
  val valueCount: Count? = null,
  /**
   * Value of extension
   */
  val valueDataRequirement: DataRequirement? = null,
  /**
   * Value of extension
   */
  val valueDate: String? = null,
  /**
   * Value of extension
   */
  val valueDateTime: String? = null,
  /**
   * Value of extension
   */
  val valueDecimal: Float? = null,
  /**
   * Value of extension
   */
  val valueDistance: Distance? = null,
  /**
   * Value of extension
   */
  val valueDosage: Dosage? = null,
  /**
   * Value of extension
   */
  val valueDuration: Duration? = null,
  /**
   * Value of extension
   */
  val valueExpression: Expression? = null,
  /**
   * Value of extension
   */
  val valueHumanName: HumanName? = null,
  /**
   * Value of extension
   */
  val valueId: String? = null,
  /**
   * Value of extension
   */
  val valueIdentifier: Identifier? = null,
  /**
   * Value of extension
   */
  val valueInstant: String? = null,
  /**
   * Value of extension
   */
  val valueInteger: Int? = null,
  /**
   * Value of extension
   */
  val valueMarkdown: String? = null,
  /**
   * Value of extension
   */
  val valueMeta: Meta? = null,
  /**
   * Value of extension
   */
  val valueMoney: Money? = null,
  /**
   * Value of extension
   */
  val valueOid: String? = null,
  /**
   * Value of extension
   */
  val valueParameterDefinition: ParameterDefinition? = null,
  /**
   * Value of extension
   */
  val valuePeriod: Period? = null,
  /**
   * Value of extension
   */
  val valuePositiveInt: Int? = null,
  /**
   * Value of extension
   */
  val valueQuantity: Quantity? = null,
  /**
   * Value of extension
   */
  val valueRange: Range? = null,
  /**
   * Value of extension
   */
  val valueRatio: Ratio? = null,
  /**
   * Value of extension
   */
  val valueReference: Reference? = null,
  /**
   * Value of extension
   */
  val valueRelatedArtifact: RelatedArtifact? = null,
  /**
   * Value of extension
   */
  val valueSampledData: SampledData? = null,
  /**
   * Value of extension
   */
  val valueSignature: Signature? = null,
  /**
   * Value of extension
   */
  val valueString: String? = null,
  /**
   * Value of extension
   */
  val valueTime: String? = null,
  /**
   * Value of extension
   */
  val valueTiming: Timing? = null,
  /**
   * Value of extension
   */
  val valueTriggerDefinition: TriggerDefinition? = null,
  /**
   * Value of extension
   */
  val valueUnsignedInt: Int? = null,
  /**
   * Value of extension
   */
  val valueUri: String? = null,
  /**
   * Value of extension
   */
  val valueUrl: String? = null,
  /**
   * Value of extension
   */
  val valueUsageContext: UsageContext? = null,
  /**
   * Value of extension
   */
  val valueUuid: String? = null
) : Element

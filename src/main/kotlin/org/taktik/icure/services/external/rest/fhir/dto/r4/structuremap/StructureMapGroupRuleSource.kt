//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.structuremap

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
 * Source inputs to the mapping
 *
 * Source inputs to the mapping.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureMapGroupRuleSource(
  /**
   * FHIRPath expression  - must be true or the mapping engine throws an error instead of completing
   */
  val check: String? = null,
  /**
   * FHIRPath expression  - must be true or the rule does not apply
   */
  val condition: String? = null,
  /**
   * Type or variable this rule applies to
   */
  val context: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueAddress: Address? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueAge: Age? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueAnnotation: Annotation? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueAttachment: Attachment? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueBase64Binary: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueBoolean: Boolean? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueCanonical: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueCode: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueCodeableConcept: CodeableConcept? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueCoding: Coding? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueContactDetail: ContactDetail? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueContactPoint: ContactPoint? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueContributor: Contributor? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueCount: Count? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueDataRequirement: DataRequirement? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueDate: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueDateTime: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueDecimal: Float? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueDistance: Distance? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueDosage: Dosage? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueDuration: Duration? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueExpression: Expression? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueHumanName: HumanName? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueId: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueIdentifier: Identifier? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueInstant: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueInteger: Int? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueMarkdown: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueMeta: Meta? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueMoney: Money? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueOid: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueParameterDefinition: ParameterDefinition? = null,
  /**
   * Default value if no value exists
   */
  val defaultValuePeriod: Period? = null,
  /**
   * Default value if no value exists
   */
  val defaultValuePositiveInt: Int? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueQuantity: Quantity? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueRange: Range? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueRatio: Ratio? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueReference: Reference? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueRelatedArtifact: RelatedArtifact? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueSampledData: SampledData? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueSignature: Signature? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueString: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueTime: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueTiming: Timing? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueTriggerDefinition: TriggerDefinition? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueUnsignedInt: Int? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueUri: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueUrl: String? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueUsageContext: UsageContext? = null,
  /**
   * Default value if no value exists
   */
  val defaultValueUuid: String? = null,
  /**
   * Optional field for this source
   */
  val element: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * first | not_first | last | not_last | only_one
   */
  val listMode: String? = null,
  /**
   * Message to put in log if source exists (FHIRPath)
   */
  val logMessage: String? = null,
  /**
   * Specified maximum cardinality (number or *)
   */
  val max: String? = null,
  /**
   * Specified minimum cardinality
   */
  val min: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Rule only applies if source has this type
   */
  val type: String? = null,
  /**
   * Named context for field, if a field is specified
   */
  val variable: String? = null
) : BackboneElement

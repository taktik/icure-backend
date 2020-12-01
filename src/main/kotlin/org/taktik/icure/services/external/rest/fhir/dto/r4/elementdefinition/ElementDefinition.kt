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
package org.taktik.icure.services.external.rest.fhir.dto.r4.elementdefinition

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
 * Definition of an element in a resource or extension
 *
 * Captures constraints on each element within the resource, profile, or extension.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ElementDefinition(
  val alias: List<String> = listOf(),
  /**
   * Base definition information for tools
   */
  val base: ElementDefinitionBase? = null,
  /**
   * ValueSet details if this is coded
   */
  val binding: ElementDefinitionBinding? = null,
  val code: List<Coding> = listOf(),
  /**
   * Comments about the use of this element
   */
  val comment: String? = null,
  val condition: List<String> = listOf(),
  val constraint: List<ElementDefinitionConstraint> = listOf(),
  /**
   * Reference to definition of content for the element
   */
  val contentReference: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueAddress: Address? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueAge: Age? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueAnnotation: Annotation? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueAttachment: Attachment? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueBase64Binary: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueBoolean: Boolean? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueCanonical: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueCode: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueCodeableConcept: CodeableConcept? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueCoding: Coding? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueContactDetail: ContactDetail? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueContactPoint: ContactPoint? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueContributor: Contributor? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueCount: Count? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueDataRequirement: DataRequirement? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueDate: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueDateTime: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueDecimal: Float? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueDistance: Distance? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueDosage: Dosage? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueDuration: Duration? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueExpression: Expression? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueHumanName: HumanName? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueId: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueIdentifier: Identifier? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueInstant: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueInteger: Int? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueMarkdown: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueMeta: Meta? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueMoney: Money? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueOid: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueParameterDefinition: ParameterDefinition? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValuePeriod: Period? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValuePositiveInt: Int? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueQuantity: Quantity? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueRange: Range? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueRatio: Ratio? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueReference: Reference? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueRelatedArtifact: RelatedArtifact? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueSampledData: SampledData? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueSignature: Signature? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueString: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueTime: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueTiming: Timing? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueTriggerDefinition: TriggerDefinition? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueUnsignedInt: Int? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueUri: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueUrl: String? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueUsageContext: UsageContext? = null,
  /**
   * Specified value if missing from instance
   */
  val defaultValueUuid: String? = null,
  /**
   * Full formal definition as narrative text
   */
  val definition: String? = null,
  val example: List<ElementDefinitionExample> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Value must be exactly this
   */
  val fixedAddress: Address? = null,
  /**
   * Value must be exactly this
   */
  val fixedAge: Age? = null,
  /**
   * Value must be exactly this
   */
  val fixedAnnotation: Annotation? = null,
  /**
   * Value must be exactly this
   */
  val fixedAttachment: Attachment? = null,
  /**
   * Value must be exactly this
   */
  val fixedBase64Binary: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedBoolean: Boolean? = null,
  /**
   * Value must be exactly this
   */
  val fixedCanonical: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedCode: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedCodeableConcept: CodeableConcept? = null,
  /**
   * Value must be exactly this
   */
  val fixedCoding: Coding? = null,
  /**
   * Value must be exactly this
   */
  val fixedContactDetail: ContactDetail? = null,
  /**
   * Value must be exactly this
   */
  val fixedContactPoint: ContactPoint? = null,
  /**
   * Value must be exactly this
   */
  val fixedContributor: Contributor? = null,
  /**
   * Value must be exactly this
   */
  val fixedCount: Count? = null,
  /**
   * Value must be exactly this
   */
  val fixedDataRequirement: DataRequirement? = null,
  /**
   * Value must be exactly this
   */
  val fixedDate: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedDateTime: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedDecimal: Float? = null,
  /**
   * Value must be exactly this
   */
  val fixedDistance: Distance? = null,
  /**
   * Value must be exactly this
   */
  val fixedDosage: Dosage? = null,
  /**
   * Value must be exactly this
   */
  val fixedDuration: Duration? = null,
  /**
   * Value must be exactly this
   */
  val fixedExpression: Expression? = null,
  /**
   * Value must be exactly this
   */
  val fixedHumanName: HumanName? = null,
  /**
   * Value must be exactly this
   */
  val fixedId: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedIdentifier: Identifier? = null,
  /**
   * Value must be exactly this
   */
  val fixedInstant: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedInteger: Int? = null,
  /**
   * Value must be exactly this
   */
  val fixedMarkdown: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedMeta: Meta? = null,
  /**
   * Value must be exactly this
   */
  val fixedMoney: Money? = null,
  /**
   * Value must be exactly this
   */
  val fixedOid: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedParameterDefinition: ParameterDefinition? = null,
  /**
   * Value must be exactly this
   */
  val fixedPeriod: Period? = null,
  /**
   * Value must be exactly this
   */
  val fixedPositiveInt: Int? = null,
  /**
   * Value must be exactly this
   */
  val fixedQuantity: Quantity? = null,
  /**
   * Value must be exactly this
   */
  val fixedRange: Range? = null,
  /**
   * Value must be exactly this
   */
  val fixedRatio: Ratio? = null,
  /**
   * Value must be exactly this
   */
  val fixedReference: Reference? = null,
  /**
   * Value must be exactly this
   */
  val fixedRelatedArtifact: RelatedArtifact? = null,
  /**
   * Value must be exactly this
   */
  val fixedSampledData: SampledData? = null,
  /**
   * Value must be exactly this
   */
  val fixedSignature: Signature? = null,
  /**
   * Value must be exactly this
   */
  val fixedString: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedTime: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedTiming: Timing? = null,
  /**
   * Value must be exactly this
   */
  val fixedTriggerDefinition: TriggerDefinition? = null,
  /**
   * Value must be exactly this
   */
  val fixedUnsignedInt: Int? = null,
  /**
   * Value must be exactly this
   */
  val fixedUri: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedUrl: String? = null,
  /**
   * Value must be exactly this
   */
  val fixedUsageContext: UsageContext? = null,
  /**
   * Value must be exactly this
   */
  val fixedUuid: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * If this modifies the meaning of other elements
   */
  val isModifier: Boolean? = null,
  /**
   * Reason that this element is marked as a modifier
   */
  val isModifierReason: String? = null,
  /**
   * Include when _summary = true?
   */
  val isSummary: Boolean? = null,
  /**
   * Name for element to display with or prompt for element
   */
  val label: String? = null,
  val mapping: List<ElementDefinitionMapping> = listOf(),
  /**
   * Maximum Cardinality (a number or *)
   */
  val max: String? = null,
  /**
   * Max length for strings
   */
  val maxLength: Int? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueDate: String? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueDateTime: String? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueDecimal: Float? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueInstant: String? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueInteger: Int? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValuePositiveInt: Int? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueQuantity: Quantity? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueTime: String? = null,
  /**
   * Maximum Allowed Value (for some types)
   */
  val maxValueUnsignedInt: Int? = null,
  /**
   * Implicit meaning when this element is missing
   */
  val meaningWhenMissing: String? = null,
  /**
   * Minimum Cardinality
   */
  val min: Int? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueDate: String? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueDateTime: String? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueDecimal: Float? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueInstant: String? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueInteger: Int? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValuePositiveInt: Int? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueQuantity: Quantity? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueTime: String? = null,
  /**
   * Minimum Allowed Value (for some types)
   */
  val minValueUnsignedInt: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * If the element must be supported
   */
  val mustSupport: Boolean? = null,
  /**
   * What the order of the elements means
   */
  val orderMeaning: String? = null,
  /**
   * Path of the element in the hierarchy of elements
   */
  val path: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternAddress: Address? = null,
  /**
   * Value must have at least these property values
   */
  val patternAge: Age? = null,
  /**
   * Value must have at least these property values
   */
  val patternAnnotation: Annotation? = null,
  /**
   * Value must have at least these property values
   */
  val patternAttachment: Attachment? = null,
  /**
   * Value must have at least these property values
   */
  val patternBase64Binary: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternBoolean: Boolean? = null,
  /**
   * Value must have at least these property values
   */
  val patternCanonical: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternCode: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternCodeableConcept: CodeableConcept? = null,
  /**
   * Value must have at least these property values
   */
  val patternCoding: Coding? = null,
  /**
   * Value must have at least these property values
   */
  val patternContactDetail: ContactDetail? = null,
  /**
   * Value must have at least these property values
   */
  val patternContactPoint: ContactPoint? = null,
  /**
   * Value must have at least these property values
   */
  val patternContributor: Contributor? = null,
  /**
   * Value must have at least these property values
   */
  val patternCount: Count? = null,
  /**
   * Value must have at least these property values
   */
  val patternDataRequirement: DataRequirement? = null,
  /**
   * Value must have at least these property values
   */
  val patternDate: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternDateTime: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternDecimal: Float? = null,
  /**
   * Value must have at least these property values
   */
  val patternDistance: Distance? = null,
  /**
   * Value must have at least these property values
   */
  val patternDosage: Dosage? = null,
  /**
   * Value must have at least these property values
   */
  val patternDuration: Duration? = null,
  /**
   * Value must have at least these property values
   */
  val patternExpression: Expression? = null,
  /**
   * Value must have at least these property values
   */
  val patternHumanName: HumanName? = null,
  /**
   * Value must have at least these property values
   */
  val patternId: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternIdentifier: Identifier? = null,
  /**
   * Value must have at least these property values
   */
  val patternInstant: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternInteger: Int? = null,
  /**
   * Value must have at least these property values
   */
  val patternMarkdown: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternMeta: Meta? = null,
  /**
   * Value must have at least these property values
   */
  val patternMoney: Money? = null,
  /**
   * Value must have at least these property values
   */
  val patternOid: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternParameterDefinition: ParameterDefinition? = null,
  /**
   * Value must have at least these property values
   */
  val patternPeriod: Period? = null,
  /**
   * Value must have at least these property values
   */
  val patternPositiveInt: Int? = null,
  /**
   * Value must have at least these property values
   */
  val patternQuantity: Quantity? = null,
  /**
   * Value must have at least these property values
   */
  val patternRange: Range? = null,
  /**
   * Value must have at least these property values
   */
  val patternRatio: Ratio? = null,
  /**
   * Value must have at least these property values
   */
  val patternReference: Reference? = null,
  /**
   * Value must have at least these property values
   */
  val patternRelatedArtifact: RelatedArtifact? = null,
  /**
   * Value must have at least these property values
   */
  val patternSampledData: SampledData? = null,
  /**
   * Value must have at least these property values
   */
  val patternSignature: Signature? = null,
  /**
   * Value must have at least these property values
   */
  val patternString: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternTime: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternTiming: Timing? = null,
  /**
   * Value must have at least these property values
   */
  val patternTriggerDefinition: TriggerDefinition? = null,
  /**
   * Value must have at least these property values
   */
  val patternUnsignedInt: Int? = null,
  /**
   * Value must have at least these property values
   */
  val patternUri: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternUrl: String? = null,
  /**
   * Value must have at least these property values
   */
  val patternUsageContext: UsageContext? = null,
  /**
   * Value must have at least these property values
   */
  val patternUuid: String? = null,
  val representation: List<String> = listOf(),
  /**
   * Why this resource has been created
   */
  val requirements: String? = null,
  /**
   * Concise definition for space-constrained presentation
   */
  val short: String? = null,
  /**
   * If this slice definition constrains an inherited slice definition (or not)
   */
  val sliceIsConstraining: Boolean? = null,
  /**
   * Name for this particular element (in a set of slices)
   */
  val sliceName: String? = null,
  /**
   * This element is sliced - slices follow
   */
  val slicing: ElementDefinitionSlicing? = null,
  val type: List<ElementDefinitionType> = listOf()
) : BackboneElement

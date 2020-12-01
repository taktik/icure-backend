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
package org.taktik.icure.fhir.entities.r4.elementdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.address.Address
import org.taktik.icure.fhir.entities.r4.age.Age
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
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
 * Example value (as defined for type)
 *
 * A sample value for this element demonstrating the type of information that would typically be
 * found in the element.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ElementDefinitionExample(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Describes the purpose of this example
   */
  val label: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueAddress: Address,
  /**
   * Value of Example (one of allowed types)
   */
  val valueAge: Age,
  /**
   * Value of Example (one of allowed types)
   */
  val valueAnnotation: Annotation,
  /**
   * Value of Example (one of allowed types)
   */
  val valueAttachment: Attachment,
  /**
   * Value of Example (one of allowed types)
   */
  val valueBase64Binary: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueBoolean: Boolean? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueCanonical: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueCode: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueCodeableConcept: CodeableConcept,
  /**
   * Value of Example (one of allowed types)
   */
  val valueCoding: Coding,
  /**
   * Value of Example (one of allowed types)
   */
  val valueContactDetail: ContactDetail,
  /**
   * Value of Example (one of allowed types)
   */
  val valueContactPoint: ContactPoint,
  /**
   * Value of Example (one of allowed types)
   */
  val valueContributor: Contributor,
  /**
   * Value of Example (one of allowed types)
   */
  val valueCount: Count,
  /**
   * Value of Example (one of allowed types)
   */
  val valueDataRequirement: DataRequirement,
  /**
   * Value of Example (one of allowed types)
   */
  val valueDate: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueDateTime: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueDecimal: Float? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueDistance: Distance,
  /**
   * Value of Example (one of allowed types)
   */
  val valueDosage: Dosage,
  /**
   * Value of Example (one of allowed types)
   */
  val valueDuration: Duration,
  /**
   * Value of Example (one of allowed types)
   */
  val valueExpression: Expression,
  /**
   * Value of Example (one of allowed types)
   */
  val valueHumanName: HumanName,
  /**
   * Value of Example (one of allowed types)
   */
  val valueId: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueIdentifier: Identifier,
  /**
   * Value of Example (one of allowed types)
   */
  val valueInstant: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueInteger: Int? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueMarkdown: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueMeta: Meta,
  /**
   * Value of Example (one of allowed types)
   */
  val valueMoney: Money,
  /**
   * Value of Example (one of allowed types)
   */
  val valueOid: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueParameterDefinition: ParameterDefinition,
  /**
   * Value of Example (one of allowed types)
   */
  val valuePeriod: Period,
  /**
   * Value of Example (one of allowed types)
   */
  val valuePositiveInt: Int? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueQuantity: Quantity,
  /**
   * Value of Example (one of allowed types)
   */
  val valueRange: Range,
  /**
   * Value of Example (one of allowed types)
   */
  val valueRatio: Ratio,
  /**
   * Value of Example (one of allowed types)
   */
  val valueReference: Reference,
  /**
   * Value of Example (one of allowed types)
   */
  val valueRelatedArtifact: RelatedArtifact,
  /**
   * Value of Example (one of allowed types)
   */
  val valueSampledData: SampledData,
  /**
   * Value of Example (one of allowed types)
   */
  val valueSignature: Signature,
  /**
   * Value of Example (one of allowed types)
   */
  val valueString: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueTime: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueTiming: Timing,
  /**
   * Value of Example (one of allowed types)
   */
  val valueTriggerDefinition: TriggerDefinition,
  /**
   * Value of Example (one of allowed types)
   */
  val valueUnsignedInt: Int? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueUri: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueUrl: String? = null,
  /**
   * Value of Example (one of allowed types)
   */
  val valueUsageContext: UsageContext,
  /**
   * Value of Example (one of allowed types)
   */
  val valueUuid: String? = null
) : Element

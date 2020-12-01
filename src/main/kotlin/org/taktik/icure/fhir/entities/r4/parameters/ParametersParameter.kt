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
package org.taktik.icure.fhir.entities.r4.parameters

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.Resource
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
 * Operation Parameter
 *
 * A parameter passed to or received from the operation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ParametersParameter(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name from the definition
   */
  val name: String? = null,
  val part: List<ParametersParameter> = listOf(),
  /**
   * If parameter is a whole resource
   */
  val resource: Resource? = null,
  /**
   * If parameter is a data type
   */
  val valueAddress: Address? = null,
  /**
   * If parameter is a data type
   */
  val valueAge: Age? = null,
  /**
   * If parameter is a data type
   */
  val valueAnnotation: Annotation? = null,
  /**
   * If parameter is a data type
   */
  val valueAttachment: Attachment? = null,
  /**
   * If parameter is a data type
   */
  val valueBase64Binary: String? = null,
  /**
   * If parameter is a data type
   */
  val valueBoolean: Boolean? = null,
  /**
   * If parameter is a data type
   */
  val valueCanonical: String? = null,
  /**
   * If parameter is a data type
   */
  val valueCode: String? = null,
  /**
   * If parameter is a data type
   */
  val valueCodeableConcept: CodeableConcept? = null,
  /**
   * If parameter is a data type
   */
  val valueCoding: Coding? = null,
  /**
   * If parameter is a data type
   */
  val valueContactDetail: ContactDetail? = null,
  /**
   * If parameter is a data type
   */
  val valueContactPoint: ContactPoint? = null,
  /**
   * If parameter is a data type
   */
  val valueContributor: Contributor? = null,
  /**
   * If parameter is a data type
   */
  val valueCount: Count? = null,
  /**
   * If parameter is a data type
   */
  val valueDataRequirement: DataRequirement? = null,
  /**
   * If parameter is a data type
   */
  val valueDate: String? = null,
  /**
   * If parameter is a data type
   */
  val valueDateTime: String? = null,
  /**
   * If parameter is a data type
   */
  val valueDecimal: Float? = null,
  /**
   * If parameter is a data type
   */
  val valueDistance: Distance? = null,
  /**
   * If parameter is a data type
   */
  val valueDosage: Dosage? = null,
  /**
   * If parameter is a data type
   */
  val valueDuration: Duration? = null,
  /**
   * If parameter is a data type
   */
  val valueExpression: Expression? = null,
  /**
   * If parameter is a data type
   */
  val valueHumanName: HumanName? = null,
  /**
   * If parameter is a data type
   */
  val valueId: String? = null,
  /**
   * If parameter is a data type
   */
  val valueIdentifier: Identifier? = null,
  /**
   * If parameter is a data type
   */
  val valueInstant: String? = null,
  /**
   * If parameter is a data type
   */
  val valueInteger: Int? = null,
  /**
   * If parameter is a data type
   */
  val valueMarkdown: String? = null,
  /**
   * If parameter is a data type
   */
  val valueMeta: Meta? = null,
  /**
   * If parameter is a data type
   */
  val valueMoney: Money? = null,
  /**
   * If parameter is a data type
   */
  val valueOid: String? = null,
  /**
   * If parameter is a data type
   */
  val valueParameterDefinition: ParameterDefinition? = null,
  /**
   * If parameter is a data type
   */
  val valuePeriod: Period? = null,
  /**
   * If parameter is a data type
   */
  val valuePositiveInt: Int? = null,
  /**
   * If parameter is a data type
   */
  val valueQuantity: Quantity? = null,
  /**
   * If parameter is a data type
   */
  val valueRange: Range? = null,
  /**
   * If parameter is a data type
   */
  val valueRatio: Ratio? = null,
  /**
   * If parameter is a data type
   */
  val valueReference: Reference? = null,
  /**
   * If parameter is a data type
   */
  val valueRelatedArtifact: RelatedArtifact? = null,
  /**
   * If parameter is a data type
   */
  val valueSampledData: SampledData? = null,
  /**
   * If parameter is a data type
   */
  val valueSignature: Signature? = null,
  /**
   * If parameter is a data type
   */
  val valueString: String? = null,
  /**
   * If parameter is a data type
   */
  val valueTime: String? = null,
  /**
   * If parameter is a data type
   */
  val valueTiming: Timing? = null,
  /**
   * If parameter is a data type
   */
  val valueTriggerDefinition: TriggerDefinition? = null,
  /**
   * If parameter is a data type
   */
  val valueUnsignedInt: Int? = null,
  /**
   * If parameter is a data type
   */
  val valueUri: String? = null,
  /**
   * If parameter is a data type
   */
  val valueUrl: String? = null,
  /**
   * If parameter is a data type
   */
  val valueUsageContext: UsageContext? = null,
  /**
   * If parameter is a data type
   */
  val valueUuid: String? = null
) : BackboneElement

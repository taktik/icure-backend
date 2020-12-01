//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.substanceamount

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.range.Range

/**
 * Chemical substances are a single substance type whose primary defining element is the molecular
 * structure. Chemical substances shall be defined on the basis of their complete covalent molecular
 * structure; the presence of a salt (counter-ion) and/or solvates (water, alcohols) is also captured.
 * Purity, grade, physical form or particle size are not taken into account in the definition of a
 * chemical substance or in the assignment of a Substance ID
 *
 * Chemical substances are a single substance type whose primary defining element is the molecular
 * structure. Chemical substances shall be defined on the basis of their complete covalent molecular
 * structure; the presence of a salt (counter-ion) and/or solvates (water, alcohols) is also captured.
 * Purity, grade, physical form or particle size are not taken into account in the definition of a
 * chemical substance or in the assignment of a Substance ID.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceAmount(
  /**
   * Used to capture quantitative values for a variety of elements. If only limits are given, the
   * arithmetic mean would be the average. If only a single definite value for a given element is
   * given, it would be captured in this field
   */
  val amountQuantity: Quantity? = null,
  /**
   * Used to capture quantitative values for a variety of elements. If only limits are given, the
   * arithmetic mean would be the average. If only a single definite value for a given element is
   * given, it would be captured in this field
   */
  val amountRange: Range? = null,
  /**
   * Used to capture quantitative values for a variety of elements. If only limits are given, the
   * arithmetic mean would be the average. If only a single definite value for a given element is
   * given, it would be captured in this field
   */
  val amountString: String? = null,
  /**
   * A textual comment on a numeric value
   */
  val amountText: String? = null,
  /**
   * Most elements that require a quantitative value will also have a field called amount type.
   * Amount type should always be specified because the actual value of the amount is often dependent
   * on it. EXAMPLE: In capturing the actual relative amounts of substances or molecular fragments it
   * is essential to indicate whether the amount refers to a mole ratio or weight ratio. For any given
   * element an effort should be made to use same the amount type for all related definitional elements
   */
  val amountType: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Reference range of possible or expected values
   */
  val referenceRange: SubstanceAmountReferenceRange? = null
) : BackboneElement

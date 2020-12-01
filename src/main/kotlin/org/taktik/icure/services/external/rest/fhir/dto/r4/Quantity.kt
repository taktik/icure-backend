//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4

/**
 * A measured or measurable amount
 *
 * A measured amount (or an amount that can potentially be measured). Note that measured amounts
 * include amounts that are not precisely quantified, including amounts involving arbitrary units and
 * floating currencies.
 */
interface Quantity : Element {
  /**
   * Coded form of the unit
   */
  val code: String?

  /**
   * < | <= | >= | > - how to understand the value
   */
  val comparator: String?

  /**
   * System that defines coded unit form
   */
  val system: String?

  /**
   * Unit representation
   */
  val unit: String?

  /**
   * Numerical value (with implicit precision)
   */
  val value: Float?
}

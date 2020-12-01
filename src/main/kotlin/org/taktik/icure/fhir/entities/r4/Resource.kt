//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4

/**
 * Base Resource
 *
 * This is the base resource type for everything.
 */
interface Resource {
  /**
   * Logical id of this artifact
   */
  val id: String?

  /**
   * A set of rules under which this content was created
   */
  val implicitRules: String?

  /**
   * Language of the resource content
   */
  val language: String?

  /**
   * Metadata about the resource
   */
  val meta: Meta?
}

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4

import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Base for all elements
 *
 * Base definition for all elements in a resource.
 */
interface Element {
  val extension: List<Extension>

  /**
   * Unique id for inter-element referencing
   */
  val id: String?
}

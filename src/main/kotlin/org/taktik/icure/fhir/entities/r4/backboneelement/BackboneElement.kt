//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.backboneelement

import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Base for elements defined inside a resource
 *
 * Base definition for all elements that are defined inside a resource - but not those in a data
 * type.
 */
interface BackboneElement : Element {
  val modifierExtension: List<Extension>
}

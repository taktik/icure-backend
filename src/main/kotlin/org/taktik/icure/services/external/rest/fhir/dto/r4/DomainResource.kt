//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4

import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative

/**
 * A resource with narrative, extensions, and contained resources
 *
 * A resource that includes narrative, extensions, and contained resources.
 */
interface DomainResource : Resource {
  val contained: List<Resource>

  val extension: List<Extension>

  val modifierExtension: List<Extension>

  /**
   * Text summary of the resource, for human interpretation
   */
  val text: Narrative?
}

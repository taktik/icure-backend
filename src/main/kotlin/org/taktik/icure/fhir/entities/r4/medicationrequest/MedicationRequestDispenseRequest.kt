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
package org.taktik.icure.fhir.entities.r4.medicationrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.duration.Duration
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Medication supply authorization
 *
 * Indicates the specific details for the dispense or medication supply part of a medication request
 * (also known as a Medication Prescription or Medication Order).  Note that this information is not
 * always sent with the order.  There may be in some settings (e.g. hospitals) institutional or system
 * support for completing the dispense details in the pharmacy department.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationRequestDispenseRequest(
  /**
   * Minimum period of time between dispenses
   */
  val dispenseInterval: Duration? = null,
  /**
   * Number of days supply per dispense
   */
  val expectedSupplyDuration: Duration? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * First fill details
   */
  val initialFill: MedicationRequestDispenseRequestInitialFill? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Number of refills authorized
   */
  val numberOfRepeatsAllowed: Int? = null,
  /**
   * Intended dispenser
   */
  val performer: Reference? = null,
  /**
   * Amount of medication to supply per dispense
   */
  val quantity: Quantity? = null,
  /**
   * Time period supply is authorized for
   */
  val validityPeriod: Period? = null
) : BackboneElement

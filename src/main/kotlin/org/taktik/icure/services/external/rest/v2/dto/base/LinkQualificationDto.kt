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

package org.taktik.icure.services.external.rest.v2.dto.base

//narrower means that the linked codes have a narrower interpretation
//parent means that the linked code(s) is the parent of this code
//basedOn means a plan, proposal or order that is fulfilled in whole or in part by this event. For example, a MedicationRequest may require a patient to have laboratory test performed before it is dispensed.
//derivedFrom means the target resource that represents a measurement from which the service value is derived. For example, a calculated anion gap or a fetal measurement based on an ultrasound image.
//device means the device used to generate the service data
//focus means the actual focus of a service when it is not the patient of record representing something or someone associated with the patient such as a spouse, parent, fetus, or donor. For example, fetus observations in a mother's record.
//hasMember means this service is a group service (e.g. a battery, a panel of tests, a set of vital sign measurements) that includes the target as a member of the group
//performer means who was responsible for asserting the observed value as "true".
//specimen means the specimen that was used when this service was made
//sequence means that the linked codes are a sequence of codes that are part of the current code
//When creating a link, we encourage creating single direction links. The reverse link can be found through a view
//Favour parent over child as it is better (for conflicts) to change 5 different documents once instead of changing 5 times the same document
enum class LinkQualificationDto {
	exact, narrower, broader, approximate, sequence, parent, child, relatedCode, linkedPackage,
	relatedService, inResponseTo, replaces, transforms, transformsAndReplaces, appendsTo,
	basedOn, derivedFrom, device, focus, hasMember, performer, specimen, resultInterpreter
}

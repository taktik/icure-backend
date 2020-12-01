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

package org.taktik.icure.entities.base

/**
 * Used to represent an entity that is saved inside the CouchDB database and includes some basic properties used to describe the lifecycle of the entity.
 *
 * @property id the Id of the patient. We encourage using either a v4 UUID or a HL7 Id.
 * @property rev the revision of the patient in the database, used for conflict management / optimistic locking.
 * @property created the timestamp (unix epoch in ms) of creation of the patient, will be filled automatically if missing. Not enforced by the application server.
 * @property modified the date (unix epoch in ms) of latest modification of the patient, will be filled automatically if missing. Not enforced by the application server.
 * @property author the id of the User that has created this patient, will be filled automatically if missing. Not enforced by the application server.
 * @property responsible the id of the HealthcareParty that is responsible for this patient, will be filled automatically if missing. Not enforced by the application server.
 * @property medicalLocationId
 *
 */
interface StoredICureDocument : StoredDocument, ICureDocument<String> {
    fun solveConflictsWith(other: StoredICureDocument) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other)
}

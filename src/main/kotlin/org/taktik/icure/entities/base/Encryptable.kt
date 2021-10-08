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

import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.utils.MergeUtil.mergeMapsOfSets

/**
 *
 * @property secretForeignKeys The secretForeignKeys are filled at the to many end of a one to many relationship (for example inside Contact for the Patient -> Contacts relationship). Used when we want to find all contacts for a specific patient. These keys are in clear. You can have several to partition the medical document space.
 * @property cryptedForeignKeys The secretForeignKeys are filled at the to many end of a one to many relationship (for example inside Contact for the Patient -> Contacts relationship). Used when we want to find the patient for a specific contact. These keys are the encrypted id (using the hcParty key for the delegate) that can be found in clear inside the patient. ids encrypted using the hcParty keys.
 * @property delegations When a document is created, the responsible generates a cryptographically random master key (never to be used for something else than referencing from other entities). He/she encrypts it using his own AES exchange key and stores it as a delegation. The responsible is thus always in the delegations as well
 * @property encryptionKeys When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db. He/she encrypts it using his own AES exchange key and stores it as a delegation
 * @property encryptedSelf The base64 encoded data of this object, formatted as JSON and encrypted in AES using the random master key from encryptionKeys.
 */
interface Encryptable {
    val secretForeignKeys: Set<String>

    //Used when we want to find the patient for this contact
    //These keys are the public patient ids encrypted using the hcParty keys.
    val cryptedForeignKeys: Map<String, Set<Delegation>>

    //When a document is created, the responsible generates a cryptographically random master key (never to be used for something else than referencing from other entities)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    //The responsible is thus always in the delegations as well
    val delegations: Map<String, Set<Delegation>>

    //When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    val encryptionKeys: Map<String, Set<Delegation>>

    val encryptedSelf: String?

    fun solveConflictsWith(other: Encryptable): Map<String, Any?> {
        return mapOf(
                "secretForeignKeys" to this.secretForeignKeys + other.secretForeignKeys,
                "cryptedForeignKeys" to mergeMapsOfSets(this.cryptedForeignKeys, other.cryptedForeignKeys),
                "delegations" to mergeMapsOfSets(this.delegations, other.delegations),
                "encryptionKeys" to mergeMapsOfSets(this.encryptionKeys, other.encryptionKeys),
                "encryptedSelf" to (this.encryptedSelf ?: other.encryptedSelf)
        )
    }

}

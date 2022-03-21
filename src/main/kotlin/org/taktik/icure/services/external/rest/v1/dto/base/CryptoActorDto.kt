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

package org.taktik.icure.services.external.rest.v1.dto.base

import io.swagger.v3.oas.annotations.media.Schema

interface CryptoActorDto {
    @get:Schema(description = "For each couple of HcParties (delegator and delegate), this map contains the exchange AES key. The delegator is always this hcp, the key of the map is the id of the delegate. The AES exchange key is encrypted using RSA twice : once using this hcp public key (index 0 in the Array) and once using the other hcp public key (index 1 in the Array). For a pair of HcParties. Each HcParty always has one AES exchange key for himself.")
    val hcPartyKeys: Map<String, Array<String>>
    @get:Schema(description = "Extra AES exchange keys, usually the ones we lost access to at some point. The structure is { publicKey: { delegateId: [aesExKey_for_this, aesExKey_for_delegate] } }")
    val aesExchangeKeys: Map<String, Map<String, Array<String>>>
    @get:Schema(description = "Our private keys encrypted with our public keys. The structure is { publicKey1: { publicKey2: privateKey2_encrypted_with_publicKey1, publicKey3: privateKey3_encrypted_with_publicKey1 } }")
    val transferKeys: Map<String, Map<String, String>>
    @get:Schema(description = "The hcparty keys (first of the pair) for which we are asking a re-encryption by the delegate using our new publicKey.")
    val lostHcPartyKeys: Set<String>
    @get:Schema(description = "The privateKeyShamirPartitions are used to share this hcp's private RSA key with a series of other hcParties using Shamir's algorithm. The key of the map is the hcp Id with whom this partition has been shared. The value is \"threshold⎮partition in hex\" encrypted using the the partition's holder's public RSA key")
    val privateKeyShamirPartitions: Map<String, String>
    @get:Schema(description = "The public key of this hcp")
    val publicKey: String?
}

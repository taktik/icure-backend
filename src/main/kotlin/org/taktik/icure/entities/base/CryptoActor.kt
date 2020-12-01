package org.taktik.icure.entities.base

import org.taktik.icure.entities.utils.MergeUtil.mergeMapsOfArraysDistinct

interface CryptoActor {
    //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
    //For a pair of HcParties, this key is called the AES exchange key
    //Each HcParty always has one AES exchange key for himself
    // The map's keys are the delegate id.
    // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
    // the key encrypted using delegate's public key.
    val hcPartyKeys: Map<String, Array<String>>
    val privateKeyShamirPartitions: Map<String, String> //Format is hcpId of key that has been partitionned : "threshold|partition in hex"
    val publicKey: String?

    fun solveConflictsWith(other: CryptoActor): Map<String, Any?> {
        return mapOf(
                "hcPartyKeys" to mergeMapsOfArraysDistinct(this.hcPartyKeys, other.hcPartyKeys),
                "privateKeyShamirPartitions" to (other.privateKeyShamirPartitions + this.privateKeyShamirPartitions),
                "publicKey" to (this.publicKey ?: other.publicKey)
        )
    }
}

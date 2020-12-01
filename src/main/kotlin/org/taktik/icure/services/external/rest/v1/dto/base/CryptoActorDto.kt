package org.taktik.icure.services.external.rest.v1.dto.base


interface CryptoActorDto {
    //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
    //For a pair of HcParties, this key is called the AES exchange key
    //Each HcParty always has one AES exchange key for himself
    // The map's keys are the delegate id.
    // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
    // the key encrypted using delegate's public key.
    val hcPartyKeys: Map<String, Array<String>>
    val privateKeyShamirPartitions: Map<String, String> //Format is hcpId of key that has been partitionned : "threshold|partition in hex"
    val publicKey: String?
}

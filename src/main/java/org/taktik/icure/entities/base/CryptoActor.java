package org.taktik.icure.entities.base;

import java.util.Map;

public interface CryptoActor {
    //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
    //For a pair of HcParties, this key is called the AES exchange key
    //Each HcParty always has one AES exchange key for himself
    // The map's keys are the delegate id.
    // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
    // the key encrypted using delegate's public key.
    Map<String, String[]> getHcPartyKeys();

    void setHcPartyKeys(Map<String, String[]> hcPartyKeys);

    String getPublicKey();

    void setPublicKey(String publicKey);

}

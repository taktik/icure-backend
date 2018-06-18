package org.taktik.icure.db

import org.ektorp.CouchDbConnector
import org.taktik.icure.client.ICureHelper
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.EncryptionException
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.security.RSAKeysUtils

import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.ExecutionException;

public class Importer {

    static final def codeFiles=["BE-POSTCODES.xml","CD-ACKNOWLEDGMENT.xml","CD-ADDRESS.xml","CD-ADMINISTRATIONUNIT.xml","CD-ATC.xml","CD-BALLON-DEVICE.xml","CD-BEARING-SURFACE.xml","CD-BVT-AVAILABLEMATERIALS.xml","CD-BVT-CONSERVATIONDELAY.xml",
                                "CD-BVT-CONSERVATIONMODE.xml","CD-BVT-LATERALITY.xml","CD-BVT-PATIENTOPPOSITION.xml","CD-BVT-SAMPLETYPE.xml","CD-BVT-STATUS.xml","CD-CAREPATH.xml","CD-CERTAINTY.xml","CD-CLINICAL.xml","CD-CLINICALPLAN.xml",
                                "CD-CONSENT.xml","CD-CONTACT-PERSON.xml","CD-CURRENCY.xml","CD-DAYPERIOD.xml","CD-DIFFERENTIATIONDEGREE.xml","CD-DISCHARGETYPE.xml","CD-DRUG-CNK.xml","CD-DRUG-PRESENTATION.xml","CD-DRUG-ROUTE.xml",
                                "CD-EBIRTH-ARTIFICIALRESPIRATIONTYPE.xml","CD-EBIRTH-CAESEREANINDICATION.xml","CD-EBIRTH-CHILDPOSITION.xml","CD-EBIRTH-CONGENITALMALFORMATION.xml","CD-EBIRTH-DELIVERYWAY.xml","CD-EBIRTH-FOETALMONITORING.xml",
                                "CD-EBIRTH-NEONATALDEPTTYPE.xml","CD-EBIRTH-PLACE.xml","CD-EBIRTH-PREGNANCYORIGIN.xml","CD-EBIRTH-SPECIALVALUES.xml","CD-EMERGENCYEVALUATION.xml","CD-ENCOUNTER.xml","CD-ENCOUNTERSAFETYISSUE.xml",
                                "CD-ENCRYPTION-ACTOR.xml","CD-FED-COUNTRY.xml","CD-HCPARTY.xml","CD-HEADING-REG.xml","CD-HEADING.xml","CD-IMPLANTATION-DEVICE.xml","CD-IMPLANTATION-TYPE.xml","CD-INCAPACITY.xml","CD-INCAPACITYREASON.xml",
                                "CD-INNCLUSTER.xml","CD-ITEM-EBIRTH.xml","CD-ITEM-MAA.xml","CD-ITEM-MS.xml","CD-ITEM-MYCARENET.xml","CD-ITEM-REG.xml","CD-ITEM.xml","CD-LAB.xml","CD-LEGAL-SERVICE.xml","CD-LIFECYCLE.xml","CD-LNK.xml",
                                "CD-MAA-COVERAGETYPE.xml","CD-MAA-REFUSALJUSTIFICATION.xml","CD-MAA-REQUESTTYPE.xml","CD-MAA-RESPONSETYPE.xml","CD-MAA-TYPE.xml","CD-MEDIATYPE.xml","CD-MESSAGE.xml","CD-MKG-ADMISSION.xml","CD-MKG-DESTINATION.xml",
                                "CD-MKG-DISCHARGE.xml","CD-MKG-ORIGIN.xml","CD-MKG-REFERRER.xml","CD-MS-ADAPTATION.xml","CD-MS-MEDICATIONTYPE.xml","CD-MS-ORIGIN.xml","CD-ORTHO-APPROACH.xml","CD-ORTHO-DIAGNOSIS.xml","CD-ORTHO-GRAFT.xml",
                                "CD-ORTHO-INTERFACE.xml","CD-ORTHO-KNEE-INSERT.xml","CD-ORTHO-NAVCOM.xml","CD-ORTHO-TECHREVISION.xml","CD-ORTHO-TYPE.xml","CD-PARAMETER.xml","CD-PATIENTWILL.xml","CD-PERIODICITY.xml","CD-PROOFTYPE.xml",
                                "CD-QUANTITYPREFIX.xml","CD-REFSCOPE.xml","CD-REIMBURSEMENT-NOMENCLATURE.xml","CD-REV-COMPONENT.xml","CD-SEVERITY.xml","CD-SEX.xml","CD-SITE.xml","CD-STANDARD.xml","CD-TECHNICAL.xml","CD-TELECOM.xml",
                                "CD-TEMPORALITY.xml","CD-THERAPEUTICLINKTYPE.xml","CD-TIMEUNIT.xml","CD-TRANSACTION-MAA.xml","CD-TRANSACTION-MYCARENET.xml","CD-TRANSACTION-REG.xml","CD-TRANSACTION-TYPE.xml","CD-TRANSACTION.xml","CD-UNIT.xml",
                                "CD-URGENCY.xml","CD-VACCINE.xml","CD-VACCINEINDICATION.xml","CD-WEEKDAY.xml","ICD.xml"];

    protected Map<String, Map<String, EncryptedCryptedKey>> cachedHcDelegatePartyKeys = [:]
    protected Map<String, Map<String, EncryptedCryptedKey>> cachedHcOwnerPartyKeys = [:]

    protected Map<String,String> cachedDocSFKs = [:] // in plaintext, could be patientId:skf
    protected Map<String, KeyPair> cachedKeyPairs = [:]
    protected Map<String, HealthcareParty> cachedDoctors = [:]

    protected IDGenerator idg = new UUIDGenerator()
    protected String keyRoot
	protected String[] patV3Ids


    protected List views = []
	protected String DB_PROTOCOL = System.getProperty("dbprotocol")?:"http"
	protected String DB_HOST = System.getProperty("dbhost")?:"127.0.0.1"
    protected String DB_PORT = System.getProperty("dbport")?:5984
    protected String DEFAULT_KEY_DIR = "/Users/aduchate/Library/icure-cloud/keys"
    protected String DB_NAME = System.getProperty("dbname")?:"icure"
    protected CouchDbConnector couchdbBase
    protected CouchDbConnector couchdbPatient
    protected CouchDbConnector couchdbContact
    protected CouchDbConnector couchdbConfig


    /**
     * @return the plain skd
     * @throws EncryptionException
     */
    public static String decryptSkdInDelegate(Delegation delegation, byte[] exchangeAESKey) throws EncryptionException {
        return decryptSkdInDelegate(delegation.getKey(), exchangeAESKey);
    }

    public static String decryptSkdInDelegate(String cryptedSkd, byte[] exchangeAESKey) throws EncryptionException {
        String plainSkd;

        try {
            plainSkd = new String(
                    CryptoUtils.decryptAES(
                            decodeHex(cryptedSkd),
                            exchangeAESKey
                    ),
                    "UTF8"
            );
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage(), e);
        }

        return plainSkd;
    }

    public static byte[] decryptHcPartyKey(String cryptedHcPartyKey, PrivateKey privateKey) throws EncryptionException {
        byte[] keyAES;
        try {
            keyAES = CryptoUtils.decrypt(
                    decodeHex(cryptedHcPartyKey),
                    privateKey
            );
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage(), e);
        }

        return keyAES;
    }
    /**
     * Decodes a hex string to a byte array. The hex string can contain either upper
     * case or lower case letters.
     *
     * @param value string to be decoded
     * @return decoded byte array
     * @throws NumberFormatException If the string contains an odd number of characters
     *                               or if the characters are not valid hexadecimal values.
     */
    public static byte[] decodeHex(final String value) {
        // if string length is odd then throw exception
        if (value.length() % 2 != 0) {
            throw new NumberFormatException("odd number of characters in hex string");
        }

        byte[] bytes = new byte[value.length() / 2];
        for (int i = 0; i < value.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(value.substring(i, i + 2), 16);
        }

        return bytes;
    }
    /**
     * Produces a Writable that writes the hex encoding of the byte[]. Calling
     * toString() on this Writable returns the hex encoding as a String. The hex
     * encoding includes two characters for each byte and all letters are lower case.
     *
     * @param data byte array to be encoded
     * @return object which will write the hex encoding of the byte array
     * @see Integer#toHexString(int)
     */
    public static Writable encodeHex(final byte[] data) {
        return new Writable() {
            public Writer writeTo(Writer out) throws IOException {
                for (byte aData : data) {
                    // convert byte into unsigned hex string
                    String hexString = Integer.toHexString(aData & 0xFF);

                    // add leading zero if the length of the string is one
                    if (hexString.length() < 2) {
                        out.write("0");
                    }

                    // write hex string to writer
                    out.write(hexString);
                }
                return out;
            }

            public String toString() {
                StringWriter buffer = new StringWriter();

                try {
                    writeTo(buffer);
                } catch (IOException e) {
                    throw new StringWriterIOException(e);
                }

                return buffer.toString();
            }
        };
    }

    protected KeyPair loadKeyPair(String id) {
        try {
            def privFile = new File(keyRoot + "/" + id + "-icc-priv.2048.key")
            def pubFile = new File(keyRoot + "/" + id + "-icc-pub.2048.key")
            PublicKey pub = null
            PrivateKey priv = null

            privFile.withReader { Reader r ->
				def hex = r.text.decodeHex()
				priv = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(hex))
			}
            pubFile.withReader { Reader r -> pub = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(r.text.decodeHex())) }

            return new KeyPair(pub, priv)
        } catch (IOException e) {
            return null;
        }
    }


    protected KeyPair createKeyPair(String id) {
        def privFile = new File(keyRoot + "/" + id + "-icc-priv.2048.key")
        def pubFile = new File(keyRoot + "/" + id + "-icc-pub.2048.key")
        KeyPair drKeyPair = null

        drKeyPair = RSAKeysUtils.generateKeyPair()

        privFile.withWriter { w -> drKeyPair.private.encoded.encodeHex().writeTo(w) }
        pubFile.withWriter { w -> drKeyPair.public.encoded.encodeHex().writeTo(w) }
        return drKeyPair
    }
    /**
     * @param ownerId owner of the Patient delegation.
     * @param delegateId to be delegated to.
     * @param documentId will be encrypted with generatedKey and used as a checksum later on. Like encryptAES(patientId:generatedKey).
     * @param key it's a generated Key. If you set null, it automatically gets generated.
     * @param exchangeAESKey is an exchange AES key of owner and delegate party.
     * @throws org.taktik.icure.exceptions.EncryptionException
     */
    protected Delegation newDelegation(String ownerId, String delegateId, String documentId, String key, byte[] exchangeAESKey) throws EncryptionException {
        // generate generatedKey if null
        if (key == null) {
            key = idg.newGUID().toString();

            // keep in cache
            cachedDocSFKs[documentId] = key
        }

        // secret key document
        // key:  (docId + ":" + generatedKey) encrypted by exchangeAESKey
        String plainSkd = documentId + ":" + key;
        return newDelegation(ownerId, delegateId, plainSkd, exchangeAESKey);
    }
    /**
     *
     * @param ownerId        owner of the Patient delegation.
     * @param delegateId     to be delegated to.
     * @param plainSkd		 Plain version of concatenated documentId:key. SKD: secret key document, and key:  (docId + ":" + generatedKey) encrypted by exchangeAESKey
     * @param exchangeAESKey is an exchange AES key of owner and delegate party.
     * @throws EncryptionException
     */
    protected static Delegation newDelegation(String ownerId, String delegateId, String plainSkd, byte[] exchangeAESKey) throws EncryptionException {
        String skd;
        try {
            skd = encodeHex(
                    CryptoUtils.encryptAES(
                            plainSkd.getBytes("UTF8"),
                            exchangeAESKey
                    )
            ).toString();
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage(), e);
        }

        Delegation newDelegation = new Delegation();
        newDelegation.setOwner(ownerId);
        newDelegation.setDelegatedTo(delegateId);
        newDelegation.setKey(skd);

        return newDelegation;
    }

    protected Delegation newDelegation(String ownerId, String delegateId, String documentId, String key, PrivateKey hcPartyKey) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        return newDelegation(ownerId, delegateId, documentId, key, getOwnerHcPartyKey(ownerId, delegateId, hcPartyKey));
    }

    protected Set<String> getSecureKeys(Map<String, List<Delegation>> delegations, String delegateId, PrivateKey delegatePrivateKey) throws EncryptionException, IOException {
        List<Delegation> myDelegations = delegations.get(delegateId);
        Set<String> result = new HashSet<>();
        for (Delegation d : myDelegations) {
            byte[] exchangeAESKey = getDelegateHcPartyKey(delegateId, d.getOwner(), delegatePrivateKey);
            String secretKeyWithId;
            try {
                secretKeyWithId = new String(CryptoUtils.decryptAES(decodeHex(d.getKey()), exchangeAESKey), "UTF8");
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new EncryptionException("Exception in AES decryption", e);
            }
            if (secretKeyWithId.contains(':')) {
                result.add(secretKeyWithId.split(":")[1]);
            } else {
                throw new EncryptionException("Invalid key content");
            }
        }
        return result;
    }

    protected Set<String> getForeignKeys(Map<String, Set<Delegation>> foreignKeys, String delegateId, PrivateKey delegatePrivateKey) throws EncryptionException, IOException {
        Set<Delegation> myDelegations = foreignKeys.get(delegateId);
        Set<String> result = new HashSet<>();
        for (Delegation d : myDelegations) {
            byte[] exchangeAESKey = getDelegateHcPartyKey(delegateId, d.getOwner(), delegatePrivateKey);
            String secretKeyWithId;
            try {
                secretKeyWithId = new String(CryptoUtils.decryptAES(decodeHex(d.getKey()), exchangeAESKey), "UTF8");
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new EncryptionException("Exception in AES decryption", e);
            }
            if (secretKeyWithId.contains(':')) {
                result.add(secretKeyWithId.split(":")[1]);
            } else {
                throw new EncryptionException("Invalid key content");
            }
        }
        return result;
    }

    protected byte[] getDelegateHcPartyKey(String myId, String ownerId, PrivateKey privateKey) throws IOException, EncryptionException {
        Map<String, EncryptedCryptedKey> keyMap = cachedHcDelegatePartyKeys.get(myId);
        if (keyMap == null || keyMap.get(ownerId) == null) {
            cachedHcDelegatePartyKeys[myId] = keyMap = [:]
            this.cachedDoctors.findAll { it.value.hcPartyKeys[myId] != null }.each { hEntry ->
                keyMap[hEntry.key] = new EncryptedCryptedKey(hEntry.value.hcPartyKeys[myId][1], null)
            }
        }

        EncryptedCryptedKey k = keyMap.get(ownerId);

        return k.getDecrypted(privateKey);
    }

    protected byte[] getOwnerHcPartyKey(String myId, String delegateId, PrivateKey privateKey) throws IOException, EncryptionException, ExecutionException, NoSuchProviderException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        Map<String, EncryptedCryptedKey> keyMap = cachedHcOwnerPartyKeys.get(myId)
        if (keyMap == null || keyMap.get(delegateId) == null) {
            HealthcareParty ownerHcParty = this.cachedDoctors[myId]
            cachedHcOwnerPartyKeys[myId] =  keyMap = [:]
            ownerHcParty.hcPartyKeys.each { k,v ->
                keyMap[k] = new EncryptedCryptedKey(v[0], null)
            }
        }

        EncryptedCryptedKey k = keyMap.get(delegateId);
        if (k == null) {
            // Fetching public Keys
            PublicKey delegatePublicKey = this.cachedKeyPairs[delegateId].public
            PublicKey myPublicKey = this.cachedKeyPairs[myId].public

            // generate exchange key (plain)
            Key exchangeAESKey = CryptoUtils.generateKeyAES();

            // crypting with delegate HcParty public key
            def clearTextAESExchangeKey = exchangeAESKey.getEncoded()
            String delegateCryptedKey = ICureHelper.encodeHex(
                    CryptoUtils.encrypt(
                            clearTextAESExchangeKey,
                            delegatePublicKey
                    )
            ).toString();

            // crypting with my public key (i.e. owner)
            String myCryptedKey = ICureHelper.encodeHex(
                    CryptoUtils.encrypt(
                            clearTextAESExchangeKey,
                            myPublicKey
                    )
            ).toString();

            // update the owner (myself) in cache and server
            HealthcareParty ownerHcParty =  this.cachedDoctors[myId]
            ownerHcParty.hcPartyKeys[delegateId] = ([myCryptedKey, delegateCryptedKey] as String[])
            // 1. update the owner dr on the server
            this.couchdbBase.update(ownerHcParty)
            HealthcareParty updatedOwnerHcParty = this.couchdbBase.get(HealthcareParty.class, ownerHcParty.id)
            // 2. update the cache
            this.cachedDoctors[myId] = updatedOwnerHcParty

            // update the 'cachedHcOwnerPartyKeys'
            Map<String, EncryptedCryptedKey> existingKeyMapOfOwner = cachedHcOwnerPartyKeys[myId]
            if (existingKeyMapOfOwner == null) {
                existingKeyMapOfOwner = [:]
                cachedHcOwnerPartyKeys[myId] =  existingKeyMapOfOwner
            }
            existingKeyMapOfOwner[delegateId] = new EncryptedCryptedKey(myCryptedKey, clearTextAESExchangeKey)


            // update the 'cachedHcDelegatePartyKeys'
            Map<String, EncryptedCryptedKey> existingKeyMapOfDelegate = cachedHcDelegatePartyKeys[delegateId]
            if (existingKeyMapOfDelegate == null) {
                existingKeyMapOfDelegate = [:]
                cachedHcDelegatePartyKeys.put(delegateId, existingKeyMapOfDelegate);
            }
            existingKeyMapOfDelegate[myId] =  new EncryptedCryptedKey(delegateCryptedKey, clearTextAESExchangeKey)

            return clearTextAESExchangeKey;
        } else {
            return k.getDecrypted(privateKey);
        }
    }
    /**
     *
     * @param newDelegationCreatedObjectPostMethod this can be for example "patient/delegate/"
     * @param modificationParentObjectPostMethod this can be for example "contact/modify"
     * @throws EncryptionException
     * @throws ExecutionException
     * @throws IOException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     */
    public <T extends StoredICureDocument> T initObjectDelegations(T createdObject, StoredICureDocument parentObject, String ownerId) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        PrivateKey ownerPrivateKey = this.cachedKeyPairs[ownerId].private;

        //Initial self delegation to store a randomly secure secret key generated inside newDelegation
        Delegation selfDelegation = newDelegation(ownerId, ownerId, createdObject.getId(), cachedDocSFKs[createdObject.id], ownerPrivateKey);

        // append the self delegation to createdObject
        if (!createdObject.delegations) {createdObject.delegations =  [:]}
        createdObject.delegations[ownerId] = [selfDelegation];

        if (parentObject != null) {
            // Appending crypted Foreign Key
            Delegation delegationForCryptedForeignKey = newDelegation(ownerId, ownerId, createdObject.getId(), parentObject.getId(), ownerPrivateKey);
            createdObject.cryptedForeignKeys[ownerId] = new HashSet<>([delegationForCryptedForeignKey]);

            // Appending Secret Foreign Key
            Set<String> secureKeys = getSecureKeys(parentObject.getDelegations(), ownerId, ownerPrivateKey);
            createdObject.setSecretForeignKeys(secureKeys);
        }

        // Updating the createdObject in Database
        //this.db.update(createdObject);
        //return this.db.get(T.class, createdObject.id)
        return createdObject
    }
    /**
     *
     * @param newDelegationModifiedObjectPostMethod this can be for example "contact/delegate/"
     * @param modificationModifiedObjectPostMethod this can be for example "contact/modify"
     * @param newDelegationParentObjectPostMethod this can be for example "patient/delegate/"
     * @throws EncryptionException
     * @throws ExecutionException
     * @throws IOException
     */
    public <T extends StoredICureDocument> T appendObjectDelegations(T modifiedObject, StoredICureDocument parentObject, String ownerId, String delegateId, String secretForeignKeyOfModifiedObject, String secretForeignKeyOfParent) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        if (ownerId == delegateId) { return this.initObjectDelegations(modifiedObject, parentObject, ownerId)}
        PrivateKey ownerPrivateKey = this.cachedKeyPairs[ownerId].private;

        if (parentObject != null && secretForeignKeyOfParent == null) {
            parentObject = initObjectDelegations(parentObject, null, ownerId)
            secretForeignKeyOfParent = cachedDocSFKs[parentObject.id]
        }

        if (secretForeignKeyOfModifiedObject == null) {
            modifiedObject = initObjectDelegations(modifiedObject, parentObject, ownerId)
            secretForeignKeyOfModifiedObject = cachedDocSFKs[modifiedObject.id]
        }

        // Fetch exchange AES key from cache, and decrypt the owner delegation
        // SFK to obtain the previously generated key of the modifiedObject.
        //byte[] exchangeAESKeyOwnerOwner = getOwnerHcPartyKey(ownerId, ownerId, ownerPrivateKey);
        //String decryptedSFK = decryptSFKInDelegate(modifiedObject.delegations[ownerId][0].key, exchangeAESKeyOwnerOwner);

        byte[] exchangeAESKeyOwnerDelegate = getOwnerHcPartyKey(ownerId, delegateId, ownerPrivateKey);

        // append the new delegation to modifiedObject
        if (modifiedObject.delegations[delegateId] == null) {modifiedObject.delegations[delegateId] = []}
        modifiedObject.delegations[delegateId] << newDelegation(ownerId, delegateId, modifiedObject.id + ":" + secretForeignKeyOfModifiedObject, exchangeAESKeyOwnerDelegate);


        if (parentObject != null) {
            /* append the CryptedForeignKeys */
            modifiedObject.cryptedForeignKeys[delegateId] = new HashSet([newDelegation(ownerId, delegateId, modifiedObject.id, parentObject.id, ownerPrivateKey)])


            /* append the Secret Foreign Key */
            modifiedObject.secretForeignKeys << secretForeignKeyOfParent
        }

        // Updating the createdObject in Database
//        this.db.update(modifiedObject);
//        return this.db.get(T.class, modifiedObject.id)
        return modifiedObject
    }
    /**
     *
     * @param delegateId , we are delegate HcParty here. (myId)
     * @return secret generated keys located in patient delegation. Those which are delegated to delegate HcParty
     */
    public <T extends StoredICureDocument> List<String> getSecretKeys(T childObject, String ownerId, String delegateId) throws IOException, EncryptionException {
        // Fetching AES exchange key of owner and delagate from cache (or remote server)
//        byte[] delegateOwnerAesExchangeKey = getDelegateHcPartyKey(delegateId, ownerId, RSAKeysUtils.loadMyKeyPair(delegateId).getPrivate());
        byte[] delegateOwnerAesExchangeKey = getDelegateHcPartyKey(delegateId, ownerId, this.cachedKeyPairs[delegateId].private);

        List<String> results = new ArrayList<>();
        for (Delegation delegation : childObject.getDelegations().get(delegateId)) {
            String plainSkd = decryptSkdInDelegate(delegation.getKey(), delegateOwnerAesExchangeKey);

            // plainSkd => childObjectId:secretKey
            results.add(plainSkd.split(":")[1]);
        }

        return results;
    }

    class EncryptedCryptedKey {
        String encrypted;
        byte[] decrypted;

        public EncryptedCryptedKey(String encrypted, byte[] decrypted) {
            this.encrypted = encrypted;
            this.decrypted = decrypted;
        }

        public byte[] getDecrypted(PrivateKey privateKey) throws EncryptionException {
            if (decrypted == null) {
                decrypted = decryptHcPartyKey(encrypted, privateKey);
            }
            return decrypted;
        }
    }
}

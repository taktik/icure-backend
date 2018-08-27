package org.taktik.icure.db

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.AttachmentInputStream
import org.ektorp.CouchDbConnector
import org.ektorp.CouchDbInstance
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.commons.io.CircularByteBuffer
import org.taktik.commons.uti.UTI
import org.taktik.icure.client.ICureHelper
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.*
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.DelegationTag
import org.taktik.icure.exceptions.EncryptionException
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.security.RSAKeysUtils

import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.ExecutionException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class Importer {
    protected Map<String, Map<String, EncryptedCryptedKey>> cachedHcDelegatePartyKeys = [:]
    protected Map<String, Map<String, EncryptedCryptedKey>> cachedHcOwnerPartyKeys = [:]

    protected Map<String,String> cachedDocSFKs = [:] // in plaintext, could be patientId:skf
    protected Map<String, KeyPair> cachedKeyPairs = [:]
    protected Map<String, HealthcareParty> cachedDoctors = [:]

    protected IDGenerator idg = new UUIDGenerator()
    protected String keyRoot
    protected def tarificationsPerCode = [:]

    protected String customOwnerId

    protected String DB_USER = System.getProperty("dbuser")?:null
    protected String DB_PASSWORD = System.getProperty("dpassword")?:null
	protected String DB_PROTOCOL = System.getProperty("dbprotocol")?:"http"
	protected String DB_HOST = System.getProperty("dbhost")?:"127.0.0.1"
    protected String DB_PORT = System.getProperty("dbport")?:5984
    protected String DEFAULT_KEY_DIR = "/Users/aduchate/Library/icure-cloud/keys"
    protected String DB_NAME = System.getProperty("dbname")?:"icure"
    protected CouchDbConnector couchdbBase
    protected CouchDbConnector couchdbPatient
    protected CouchDbConnector couchdbContact
    protected CouchDbConnector couchdbConfig

    Importer() {
        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("${DB_PROTOCOL}://${DB_HOST}:" + DB_PORT).username(DB_USER).password(DB_PASSWORD).build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient)

        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbBase = dbInstance.createConnector(DB_NAME + '-base', true)
        couchdbPatient = dbInstance.createConnector(DB_NAME + '-patient', true)
        couchdbContact = dbInstance.createConnector(DB_NAME + '-healthdata', true)
        couchdbConfig = dbInstance.createConnector(DB_NAME + '-config', true)

        Security.addProvider(new BouncyCastleProvider())

        couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/Tarification").viewName("all"), Tarification.class).each { Tarification t ->
            tarificationsPerCode[t.code] = t
        }
    }

    void createAttachment(File file, Document d) {
        if (file != null) {
            def types = UTI.get(d.mainUti)?.mimeTypes
            if (file.isFile()) {
                file.withInputStream { is ->
                    def attId = d.attachmentId ? d.attachmentId?.split(/\|/)[0] : DigestUtils.sha256Hex(file.bytes)
                    couchdbContact.createAttachment(d.id, d.rev, new AttachmentInputStream(attId, is, types?.size() ? types[0] : "application/octet-stream"))

                    d = couchdbContact.get(Document.class, d.id)
                    d.attachmentId = attId

                    couchdbContact.update(d)
                }
            } else if (file.isDirectory()) {
                def cbb = new CircularByteBuffer(128000)

                Thread.start {
                    def zo = new ZipOutputStream(cbb.outputStream)

                    file.eachFileRecurse { f ->
                        def name = f.absolutePath.substring(file.absolutePath.length() - file.name.length())
                        zo.putNextEntry(new ZipEntry(f.isDirectory() ? (name + "/") : name))
                        if (f.isFile()) {
                            f.withInputStream { IOUtils.copy(it, zo) }
                            zo.closeEntry()
                        }
                    }

                    zo.close()
                }

                def attId = d.attachmentId ? d.attachmentId?.split(/\|/)[0] : "zipfile"
                couchdbContact.createAttachment(d.id, d.rev, new AttachmentInputStream(attId, cbb.inputStream, types?.size() ? types[0] : "application/octet-stream"))

                d = couchdbContact.get(Document.class, d.id)
                d.attachmentId = attId

                couchdbContact.update(d)
            }
        } else if (d.attachment != null) {
            def attId = DigestUtils.sha256Hex(d.attachment)

            UTI uti = UTI.get(d.mainUti);
            String mimeType = "application/xml"
            if (uti != null && uti.mimeTypes != null && uti.mimeTypes.size() > 0) {
                mimeType = uti.mimeTypes[0]
            }

            AttachmentInputStream a = new AttachmentInputStream(attId, new ByteArrayInputStream(d.attachment), mimeType)

            couchdbContact.createAttachment(d.id, d.rev, a)
            d = couchdbContact.get(Document.class, d.id)
            d.attachmentId = attId

            couchdbContact.update(d)
        }
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    void doImport(Collection<User> users, Collection<HealthcareParty> parties, Collection<Patient> patients, Map<String, List<Invoice>> invoices,
                  Map<String, List<Contact>> contacts, Map<String, List<HealthElement>> healthElements, Map<String, List<Form>> forms,
                  Collection<Message> messages, Map<String, Collection<String>> messageDocs, Collection<Map> docs, Collection<AccessLog> accessLogs) {

        def startImport = System.currentTimeMillis()

        print("Importing accessLogs... ")
        new ArrayList(accessLogs).collate(1000).each { couchdbPatient.executeBulk(it) }
        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()

        print("Importing doctors... ")
        parties.each { dr ->
            cachedDoctors[dr.id] = dr

            if (users*.healthcarePartyId.contains(dr.id)) {
                /**** Cryptography *****/
                def keyPair = loadKeyPair(dr.id)
                if (!keyPair) {
                    keyPair = createKeyPair(dr.id)
                }
                KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES", "BC")
                aesKeyGenerator.init(256)
                Key encryptKey = aesKeyGenerator.generateKey()

                def crypted = CryptoUtils.encrypt(encryptKey.encoded, keyPair.public).encodeHex()

                dr.hcPartyKeys = [:]
                dr.hcPartyKeys[dr.id] = ([crypted, crypted] as String[])

                dr.setPublicKey(keyPair.public.encoded.encodeHex().toString())

                cachedKeyPairs[dr.id] = keyPair
            }
        }

        def delegates = new ArrayList<String>(cachedKeyPairs.keySet())

        users.each { user ->
            if (user.login) {
                user.autoDelegations[DelegationTag.all] = new HashSet<>(delegates.findAll { it != user.healthcarePartyId })
            }
        }

        couchdbBase.executeBulk(parties)
        couchdbBase.executeBulk(users)

        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        println("Importing patients... ")

        println("Delegates are : ${delegates.join(',')}")

        String dbOwnerId
        if(customOwnerId != null) {
            dbOwnerId = customOwnerId
            this.cachedDoctors[dbOwnerId] = couchdbBase.get(HealthcareParty, dbOwnerId)
            this.cachedKeyPairs[dbOwnerId] = loadKeyPair(dbOwnerId)
            if (!this.cachedKeyPairs[dbOwnerId]  ) {
                println("ERROR: key not found for customOwnerId")
            }
        } else {
            dbOwnerId = delegates[0]
        }
        println("OwnerId = ${dbOwnerId}")

        def formsPerId = [:]
        forms.each { pid, fs ->
            fs.each { f ->
                formsPerId[f.id] = (formsPerId[f.id] ?: []) + [pid]
            }
        }

        def pMessages = [:]
        messages.each { m ->
            formsPerId[m.formId].each { pid ->
                pMessages[pid] = (pMessages[pid] ?: []) + [m]
            }
        }

        def pats = []
        patients.each { p ->
            /**** Delegations ****/
            delegates.each { delegateId -> p = this.appendObjectDelegations(p, null, dbOwnerId, delegateId, this.cachedDocSFKs[p.id], null) }

            def pCtcs = contacts[p.id] ?: []
            def pHes = healthElements[p.id] ?: []
            def pForms = forms[p.id] ?: []
            def pInvoices = invoices[p.id] ?: []
            def ppMessages = pMessages[p.id] ?: []

            contacts.remove(p.id)
            healthElements.remove(p.id)
            forms.remove(p.id)
            invoices.remove(p.id)
            pMessages.remove(p.id)


            pCtcs?.each { Contact c ->
                delegates.each { delegateId -> c = this.appendObjectDelegations(c, p, dbOwnerId, delegateId, this.cachedDocSFKs[c.id], this.cachedDocSFKs[p.id]) as Contact }
                c.services.each { s ->
                    s.content.values().each { cnt ->
                        if (cnt.binaryValue?.length) {
                            cnt.binaryValue = new File(new String(cnt.binaryValue, 'UTF8')).bytes
                        }
                    }
                }
            }

            pHes?.each { HealthElement e -> delegates.each { delegateId -> e = this.appendObjectDelegations(e, p, dbOwnerId, delegateId, this.cachedDocSFKs[e.id], this.cachedDocSFKs[p.id]) as HealthElement } }
            pForms?.each { f -> delegates.each { delegateId -> f = this.appendObjectDelegations(f, p, dbOwnerId, delegateId, this.cachedDocSFKs[f.id], this.cachedDocSFKs[p.id]) as Form } }
            pInvoices?.each { iv ->
                iv.invoicingCodes.each {
                    if (it.code && !it.tarificationId && tarificationsPerCode[it.code]) {
                        it.tarificationId = tarificationsPerCode[it.code].id
                    }
                }

                delegates.each { delegateId -> iv = this.appendObjectDelegations(iv, p, dbOwnerId, delegateId, this.cachedDocSFKs[iv.id], this.cachedDocSFKs[p.id]) as Invoice }
            }
            ppMessages?.each { Message m -> delegates.each { delegateId -> m = this.appendObjectDelegations(m, p, dbOwnerId, delegateId, this.cachedDocSFKs[m.id], this.cachedDocSFKs[p.id]) as Message } }

            pats << [p, pCtcs, pHes, pForms, pInvoices]

            if (pats.size() == 10) {
                couchdbPatient.executeBulk(pats.collect { it[0] })
                couchdbContact.executeBulk(pats.collect { it[1] + it[2] + it[3] + it[4] }.flatten())
                print(".")
                pats.clear()
            }
        }

        if (pats.size()) {
            couchdbPatient.executeBulk(pats.collect { it[0] })
            couchdbContact.executeBulk(pats.collect { it[1] + it[2] + it[3] + it[4] }.flatten())
        }

        //Already start indexation
        Thread.start {
            try {
                couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(couchdbContact.path()).designDocId("_design/Contact").viewName("all").limit(1), String.class).each {
                }
            } catch (Exception ignored) {
            }
        }

        println("\n completed in " + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        print("Importing messages... ")

        messages.each { Message mm ->
            if (!this.cachedDocSFKs[mm.id]) {
                delegates.each { delegateId -> mm = this.appendObjectDelegations(mm, null, dbOwnerId, delegateId, this.cachedDocSFKs[mm.id], null) }
            }
            def mDocs = messageDocs[mm.id]

            mDocs?.each { Map dd ->
                Document d = dd.doc
                delegates.each { delegateId -> dd.doc = d = this.appendObjectDelegations(d, mm, delegates[0], delegateId, this.cachedDocSFKs[d.id], this.cachedDocSFKs[mm.id]) as Document }
            }
        }
        new ArrayList(messages).collate(100).each { couchdbContact.executeBulk(it) }

        println("\n completed in " + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        print("Importing documents... ")

        docs.each { dd ->
            Document d = dd.doc
            if (!this.cachedDocSFKs[d.id]) {
                delegates.each { delegateId -> dd.doc = d = this.appendObjectDelegations(d, null, dbOwnerId, delegateId, this.cachedDocSFKs[d.id], null) }
            }
        }
        new ArrayList(docs).collate(1000).each { couchdbContact.executeBulk(it*.doc) }

        docs.each { dd ->
            createAttachment(dd.file, dd.doc)
        }

        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
    }

    /**
     * @return the plain skd
     * @throws EncryptionException
     */
    static String decryptSkdInDelegate(Delegation delegation, byte[] exchangeAESKey) throws EncryptionException {
        return decryptSkdInDelegate(delegation.getKey(), exchangeAESKey)
    }

    static String decryptSkdInDelegate(String cryptedSkd, byte[] exchangeAESKey) throws EncryptionException {
        String plainSkd

        try {
            plainSkd = new String(
                    CryptoUtils.decryptAES(
                            decodeHex(cryptedSkd),
                            exchangeAESKey
                    ),
                    "UTF8"
            )
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage(), e)
        }

        return plainSkd
    }

    static byte[] decryptHcPartyKey(String cryptedHcPartyKey, PrivateKey privateKey) throws EncryptionException {
        byte[] keyAES
        try {
            keyAES = CryptoUtils.decrypt(
                    decodeHex(cryptedHcPartyKey),
                    privateKey
            )
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage(), e)
        }

        return keyAES
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
    static byte[] decodeHex(final String value) {
        // if string length is odd then throw exception
        if (value.length() % 2 != 0) {
            throw new NumberFormatException("odd number of characters in hex string")
        }

        byte[] bytes = new byte[value.length() / 2]
        for (int i = 0; i < value.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(value.substring(i, i + 2), 16)
        }

        return bytes
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
    static Writable encodeHex(final byte[] data) {
        return new Writable() {
            Writer writeTo(Writer out) throws IOException {
                for (byte aData : data) {
                    // convert byte into unsigned hex string
                    String hexString = Integer.toHexString(aData & 0xFF)

                    // add leading zero if the length of the string is one
                    if (hexString.length() < 2) {
                        out.write("0")
                    }

                    // write hex string to writer
                    out.write(hexString)
                }
                return out
            }

            String toString() {
                StringWriter buffer = new StringWriter()

                try {
                    writeTo(buffer)
                } catch (IOException e) {
                    throw new StringWriterIOException(e)
                }

                return buffer.toString()
            }
        }
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
            return null
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
            key = idg.newGUID().toString()

            // keep in cache
            cachedDocSFKs[documentId] = key
        }

        // secret key document
        // key:  (docId + ":" + generatedKey) encrypted by exchangeAESKey
        String plainSkd = documentId + ":" + key
        return newDelegation(ownerId, delegateId, plainSkd, exchangeAESKey)
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
        String skd
        try {
            skd = encodeHex(
                    CryptoUtils.encryptAES(
                            plainSkd.getBytes("UTF8"),
                            exchangeAESKey
                    )
            ).toString()
        } catch (Exception e) {
            throw new EncryptionException(e.getMessage(), e)
        }

        Delegation newDelegation = new Delegation()
        newDelegation.setOwner(ownerId)
        newDelegation.setDelegatedTo(delegateId)
        newDelegation.setKey(skd)

        return newDelegation
    }

    protected Delegation newDelegation(String ownerId, String delegateId, String documentId, String key, PrivateKey hcPartyKey) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        return newDelegation(ownerId, delegateId, documentId, key, getOwnerHcPartyKey(ownerId, delegateId, hcPartyKey))
    }

    protected Set<String> getSecureKeys(Map<String, Set<Delegation>> delegations, String delegateId, PrivateKey delegatePrivateKey) throws EncryptionException, IOException {
        Set<Delegation> myDelegations = delegations.get(delegateId)
        Set<String> result = new HashSet<>()
        for (Delegation d : myDelegations) {
            byte[] exchangeAESKey = getDelegateHcPartyKey(delegateId, d.getOwner(), delegatePrivateKey)
            String secretKeyWithId
            try {
                secretKeyWithId = new String(CryptoUtils.decryptAES(decodeHex(d.getKey()), exchangeAESKey), "UTF8")
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new EncryptionException("Exception in AES decryption", e)
            }
            if (secretKeyWithId.contains(':')) {
                result.add(secretKeyWithId.split(":")[1])
            } else {
                throw new EncryptionException("Invalid key content")
            }
        }
        return result
    }

    protected Set<String> getForeignKeys(Map<String, Set<Delegation>> foreignKeys, String delegateId, PrivateKey delegatePrivateKey) throws EncryptionException, IOException {
        Set<Delegation> myDelegations = foreignKeys.get(delegateId)
        Set<String> result = new HashSet<>()
        for (Delegation d : myDelegations) {
            byte[] exchangeAESKey = getDelegateHcPartyKey(delegateId, d.getOwner(), delegatePrivateKey)
            String secretKeyWithId
            try {
                secretKeyWithId = new String(CryptoUtils.decryptAES(decodeHex(d.getKey()), exchangeAESKey), "UTF8")
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new EncryptionException("Exception in AES decryption", e)
            }
            if (secretKeyWithId.contains(':')) {
                result.add(secretKeyWithId.split(":")[1])
            } else {
                throw new EncryptionException("Invalid key content")
            }
        }
        return result
    }

    protected byte[] getDelegateHcPartyKey(String myId, String ownerId, PrivateKey privateKey) throws IOException, EncryptionException {
        Map<String, EncryptedCryptedKey> keyMap = cachedHcDelegatePartyKeys.get(myId)
        if (keyMap == null || keyMap.get(ownerId) == null) {
            cachedHcDelegatePartyKeys[myId] = keyMap = [:]
            this.cachedDoctors.findAll { it.value.hcPartyKeys[myId] != null }.each { hEntry ->
                keyMap[hEntry.key] = new EncryptedCryptedKey(hEntry.value.hcPartyKeys[myId][1], null)
            }
        }

        EncryptedCryptedKey k = keyMap.get(ownerId)

        return k.getDecrypted(privateKey)
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

        EncryptedCryptedKey k = keyMap.get(delegateId)
        if (k == null) {
            // Fetching public Keys
            PublicKey delegatePublicKey = this.cachedKeyPairs[delegateId].public
            PublicKey myPublicKey = this.cachedKeyPairs[myId].public

            // generate exchange key (plain)
            Key exchangeAESKey = CryptoUtils.generateKeyAES()

            // crypting with delegate HcParty public key
            def clearTextAESExchangeKey = exchangeAESKey.getEncoded()
            String delegateCryptedKey = ICureHelper.encodeHex(
                    CryptoUtils.encrypt(
                            clearTextAESExchangeKey,
                            delegatePublicKey
                    )
            ).toString()

            // crypting with my public key (i.e. owner)
            String myCryptedKey = ICureHelper.encodeHex(
                    CryptoUtils.encrypt(
                            clearTextAESExchangeKey,
                            myPublicKey
                    )
            ).toString()

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
                cachedHcDelegatePartyKeys.put(delegateId, existingKeyMapOfDelegate)
            }
            existingKeyMapOfDelegate[myId] =  new EncryptedCryptedKey(delegateCryptedKey, clearTextAESExchangeKey)

            return clearTextAESExchangeKey
        } else {
            return k.getDecrypted(privateKey)
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
    def <T extends StoredICureDocument> T initObjectDelegations(T createdObject, StoredICureDocument parentObject, String ownerId) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        PrivateKey ownerPrivateKey = this.cachedKeyPairs[ownerId].private

        //Initial self delegation to store a randomly secure secret key generated inside newDelegation
        Delegation selfDelegation = newDelegation(ownerId, ownerId, createdObject.getId(), cachedDocSFKs[createdObject.id], ownerPrivateKey)

        // append the self delegation to createdObject
        if (!createdObject.delegations) {createdObject.delegations =  [:]}
        createdObject.delegations[ownerId] = new HashSet<Delegation>([selfDelegation])

        if (parentObject != null) {
            // Appending crypted Foreign Key
            Delegation delegationForCryptedForeignKey = newDelegation(ownerId, ownerId, createdObject.getId(), parentObject.getId(), ownerPrivateKey)
            createdObject.cryptedForeignKeys[ownerId] = new HashSet<>([delegationForCryptedForeignKey])

            // Appending Secret Foreign Key
            Set<String> secureKeys = getSecureKeys(parentObject.getDelegations(), ownerId, ownerPrivateKey)
            createdObject.setSecretForeignKeys(secureKeys)
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
    def <T extends StoredICureDocument> T appendObjectDelegations(T modifiedObject, StoredICureDocument parentObject, String ownerId, String delegateId, String secretForeignKeyOfModifiedObject, String secretForeignKeyOfParent) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        if (ownerId == delegateId) { return this.initObjectDelegations(modifiedObject, parentObject, ownerId)}
        PrivateKey ownerPrivateKey = this.cachedKeyPairs[ownerId].private

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

        byte[] exchangeAESKeyOwnerDelegate = getOwnerHcPartyKey(ownerId, delegateId, ownerPrivateKey)

        // append the new delegation to modifiedObject
        if (modifiedObject.delegations[delegateId] == null) {modifiedObject.delegations[delegateId] = new HashSet<>()}
        modifiedObject.delegations[delegateId] << newDelegation(ownerId, delegateId, modifiedObject.id + ":" + secretForeignKeyOfModifiedObject, exchangeAESKeyOwnerDelegate)


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
    def <T extends StoredICureDocument> List<String> getSecretKeys(T childObject, String ownerId, String delegateId) throws IOException, EncryptionException {
        // Fetching AES exchange key of owner and delagate from cache (or remote server)
//        byte[] delegateOwnerAesExchangeKey = getDelegateHcPartyKey(delegateId, ownerId, RSAKeysUtils.loadMyKeyPair(delegateId).getPrivate());
        byte[] delegateOwnerAesExchangeKey = getDelegateHcPartyKey(delegateId, ownerId, this.cachedKeyPairs[delegateId].private)

        List<String> results = new ArrayList<>()
        for (Delegation delegation : childObject.getDelegations().get(delegateId)) {
            String plainSkd = decryptSkdInDelegate(delegation.getKey(), delegateOwnerAesExchangeKey)

            // plainSkd => childObjectId:secretKey
            results.add(plainSkd.split(":")[1])
        }

        return results
    }

    class EncryptedCryptedKey {
        String encrypted
        byte[] decrypted

        EncryptedCryptedKey(String encrypted, byte[] decrypted) {
            this.encrypted = encrypted
            this.decrypted = decrypted
        }

        byte[] getDecrypted(PrivateKey privateKey) throws EncryptionException {
            if (decrypted == null) {
                decrypted = decryptHcPartyKey(encrypted, privateKey)
            }
            return decrypted
        }
    }
}
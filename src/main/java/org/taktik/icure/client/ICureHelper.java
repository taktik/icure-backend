/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.exceptions.EncryptionException;
import org.taktik.icure.exceptions.ICureException;
import org.taktik.icure.security.CryptoUtils;
import org.taktik.icure.security.RSAKeysUtils;
import org.taktik.icure.services.external.rest.handlers.DiscriminatedTypeAdapter;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.AuthenticationResponse;
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureDto;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.lang.annotation.Annotation;
import java.security.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.HOURS;

public class ICureHelper {
    private static final Logger log = LoggerFactory.getLogger(ICureHelper.class);

    private static IDGenerator idg = new UUIDGenerator();

    private CloseableHttpClient client;
	private ContactHelper contactHelper;
	private HttpClientContext context;
	private DocumentHelper documentHelper;
	private Gson gson;
	private HealthcarePartyHelper healthcarePartyHelper;
	private CodeHelper codeHelper;
	private HealthElementHelper healthElementHelper;
	private MessageHelper messageHelper;
	private PatientHelper patientHelper;
	private HttpHost targetHost;
	private UserHelper userHelper;
	private String username;
	private String password;

	/**
	 *  The structure is as follows:
	 *  {
	 *  	delegateId1: {
	 *  	 	ownerId1: EncryptedCryptedKey, //encrypted/decrypted hcparty key by public key of delegateId1
	 *  	 	...
	 *  	},
	 *  	...
	 *  }
	 */
    private Cache<String, Map<String, EncryptedCryptedKey>> hcDelegatePartyKeysCache = CacheBuilder.newBuilder().expireAfterWrite(12, HOURS).build();

	/**
	 *  The structure is as follows:
	 *  {
	 *  	ownerId1: {
	 *  	 	delegateId1: EncryptedCryptedKey, //encrypted/decrypted hcparty key  by delegateId1's public key
	 *  	 	...
	 *  	},
	 *  	...
	 *  }
	 */
    private Cache<String, Map<String, EncryptedCryptedKey>> hcOwnerPartyKeysCache = CacheBuilder.newBuilder().expireAfterWrite(12, HOURS).build();

	public ICureHelper() {}

    public ICureHelper(String user, String password, String host, Integer port, Boolean secure, Boolean withBasicAuth) {
		this.username = user;
		this.password = password;
		HttpClientBuilder httpClientBuilder;
		if (withBasicAuth) {
			if (host == null) {
				host = "localhost";
			}
			if (port == null) {
				port = 443;
			}
			if (secure == null) {
				secure = true;
			}

			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
					new AuthScope(host, port),
					new UsernamePasswordCredentials(user, password));

			httpClientBuilder = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider);

			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();
			// Generate BASIC scheme object and add it to the local auth cache
			BasicScheme basicAuth = new BasicScheme();

			targetHost = new HttpHost(host, port, secure ? "https" : "http");
			authCache.put(targetHost, basicAuth);

			// Add AuthCache to the execution context
			context = HttpClientContext.create();
			context.setCredentialsProvider(credsProvider);
			context.setAuthCache(authCache);
		} else {
			targetHost = new HttpHost(host, port, secure ? "https" : "http");
			httpClientBuilder = HttpClientBuilder.create();
		}

        client = httpClientBuilder.build();

		// Entity clients instantiations
	    contactHelper = new ContactHelper(this);
	    documentHelper = new DocumentHelper(this);
	    healthcarePartyHelper = new HealthcarePartyHelper(this);
	    healthElementHelper = new HealthElementHelper(this);
	    messageHelper = new MessageHelper(this);
	    patientHelper = new PatientHelper(this);
	    userHelper = new UserHelper(this);
		codeHelper = new CodeHelper(this);
    }

	public ContactHelper getContactHelper() {
		return contactHelper;
	}

	public DocumentHelper getDocumentHelper() {
		return documentHelper;
	}

	public MessageHelper getMessageHelper() {
		return messageHelper;
	}

	public PatientHelper getPatientHelper() {
		return patientHelper;
	}

	public HealthcarePartyHelper getHealthcarePartyHelper() {
		return healthcarePartyHelper;
	}

	public UserHelper getUserHelper() {
		return userHelper;
	}

	public HealthElementHelper getHealthElementHelper() {
		return healthElementHelper;
	}

	public CodeHelper getCodeHelper() {
		return codeHelper;
	}

	public Gson getGson() {
        if (gson == null) {
            final GsonBuilder gsonBuilder = new GsonBuilder();

			gsonBuilder.serializeSpecialFloatingPointValues().registerTypeAdapter(PaginatedDocumentKeyIdPair.class, (JsonDeserializer<PaginatedDocumentKeyIdPair>) (json, typeOfT, context) -> {
				Map<String,Object> obj = context.deserialize(json,Map.class);
				return new PaginatedDocumentKeyIdPair<>((List<String>)obj.get("startKey"), (String)obj.get("startKeyDocId"));
			}).registerTypeAdapter(Filter.class, new DiscriminatedTypeAdapter<>(Filter.class));

            gson = gsonBuilder.create();
        }
        return gson;
    }

    public String marshal(Object parameter) throws IOException {
        StringWriter writer = new StringWriter();

		Class polymorphismRoot = Object.class;
		if (parameter.getClass().getAnnotations().length>0) {
			for (Annotation ann:parameter.getClass().getAnnotations()) {
				if (ann instanceof JsonPolymorphismRoot) {
					polymorphismRoot = ((JsonPolymorphismRoot) ann).value();
					break;
				}
			}
		}

        getGson().toJson(parameter, polymorphismRoot, writer);
        return writer.toString();
    }


    public String doRestGET(String method) throws IOException {
        HttpGet request = new HttpGet("http://localhost:16043/rest/v1/" + method);

        HttpResponse response = client.execute(targetHost, request, context);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(response.getEntity().getContent(), byteArrayOutputStream);

        String responseString = new String(byteArrayOutputStream.toByteArray(), "UTF8");

        if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 203) {
            throw new RuntimeException(responseString);
        }

        return responseString;
    }


    public String doRestPOST(String method, Object parameter) throws IOException, RuntimeException {
        HttpPost request = new HttpPost("http://localhost:16043/rest/v1/" + method);

        if (parameter != null) {
            request.setEntity(new StringEntity(marshal(parameter), ContentType.APPLICATION_JSON));
        }

        HttpResponse response = client.execute(targetHost, request, context);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(response.getEntity().getContent(), byteArrayOutputStream);

        String responseString = new String(byteArrayOutputStream.toByteArray(), "UTF8");

        if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 203) {
            throw new RuntimeException(responseString);
        }

        return responseString;
    }


	public String doRestPUT(String method, Object parameter) throws IOException, RuntimeException {
		HttpPut request = new HttpPut("http://localhost:16043/rest/v1/" + method);

		if (parameter != null) {
			request.setEntity(new StringEntity(marshal(parameter), ContentType.APPLICATION_JSON));
		}

		HttpResponse response = client.execute(targetHost, request, context);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		IOUtils.copy(response.getEntity().getContent(), byteArrayOutputStream);

		String responseString = new String(byteArrayOutputStream.toByteArray(), "UTF8");

		if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 203) {
			throw new RuntimeException(responseString);
		}

		return responseString;
	}

    /**
     * @param ownerId        owner of the Patient delegation.
     * @param delegateId     to be delegated to.
     * @param documentId     will be encrypted with generatedKey and used as a checksum later on. Like encryptAES(patientId:generatedKey).
     * @param key            it's a generated Key. If you set null, it automatically gets generated.
     * @param exchangeAESKey is an exchange AES key of owner and delegate party.
     * @throws EncryptionException
     */
    private DelegationDto newDelegation(String ownerId, String delegateId, String documentId, String key, byte[] exchangeAESKey) throws EncryptionException {
		// generate generatedKey if null
		if (key == null) {
			key = idg.newGUID().toString();
		}

		// Secret Foreign Key
		// key:  (docId + ":" + generatedKey) encrypted by exchangeAESKey
		String plainSFK = documentId + ":" + key;
		return newDelegation(ownerId, delegateId,  plainSFK, exchangeAESKey);
    }

	/**
	 *
	 * @param ownerId        owner of the Patient delegation.
	 * @param delegateId     to be delegated to.
	 * @param plainSFK		 Plain version of concatenated documentId:key. SFK: Secret Foreign Key, and key:  (docId + ":" + generatedKey) encrypted by exchangeAESKey
	 * @param exchangeAESKey is an exchange AES key of owner and delegate party.
	 * @throws EncryptionException
	 */
	private DelegationDto newDelegation(String ownerId, String delegateId, String plainSFK, byte[] exchangeAESKey) throws EncryptionException {
		String SFK;
		try {
			SFK = encodeHex(
					CryptoUtils.encryptAES(
							plainSFK.getBytes("UTF8"),
							exchangeAESKey
					)
			).toString();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EncryptionException(e.getMessage(), e);
		}

		DelegationDto newDelegation = new DelegationDto();
		newDelegation.setOwner(ownerId);
		newDelegation.setDelegatedTo(delegateId);
		newDelegation.setKey(SFK);

		return newDelegation;
	}

	private DelegationDto newDelegation(String ownerId, String delegateId, String documentId, String key, PrivateKey hcPartyKey) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        return newDelegation(ownerId, delegateId, documentId, key, getOwnerHcPartyKey(ownerId, delegateId, hcPartyKey));
    }

    /**
     * @return the plain SFK (Secret Foreign Key)
     * @throws EncryptionException
     */
    public String decryptSFKInDelegate(DelegationDto delegation, byte[] exchangeAESKey) throws EncryptionException {
        return decryptSFKInDelegate(delegation.getKey(), exchangeAESKey);
    }

	public String decryptSFKInDelegate(String cryptedSfk, byte[] exchangeAESKey) throws EncryptionException {
		String plainSFK; // Secret Foreign Key

		try {
			plainSFK = new String(
					CryptoUtils.decryptAES(
							decodeHex(cryptedSfk),
							exchangeAESKey
					),
					"UTF8"
			);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EncryptionException(e.getMessage(), e);
		}

		return plainSFK;
	}

	public byte[] decryptHcPartyKey(String cryptedHcPartyKey, PrivateKey privateKey) throws EncryptionException {
        byte[] keyAES;
        try {
            keyAES = CryptoUtils.decrypt(
                    decodeHex(cryptedHcPartyKey),
                    privateKey
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new EncryptionException(e.getMessage(), e);
        }

        return keyAES;
    }


    //Init secure keys based on my keys for a document freshly created by me
    private Set<String> getSecureKeys(Map<String, List<DelegationDto>> delegations, String delegateId, PrivateKey delegatePrivateKey) throws EncryptionException, IOException {
        List<DelegationDto> myDelegations = delegations.get(delegateId);
        Set<String> result = new HashSet<>();
        for (DelegationDto d : myDelegations) {
            byte[] exchangeAESKey = getDelegateHcPartyKey(delegateId, d.getOwner(), delegatePrivateKey);
            String secretKeyWithId;
            try {
                secretKeyWithId = new String(CryptoUtils.decryptAES(decodeHex(d.getKey()), exchangeAESKey), "UTF8");
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                throw new EncryptionException("Exception in AES decryption", e);
            }
            result.add(secretKeyWithId.split(":")[1]);
        }
        return result;
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
	public static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String encodeHex(final byte[] data) {
		char[] hexChars = new char[data.length * 2];
		for ( int j = 0; j < data.length; j++ ) {
			int v = data[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
    }

	/**
	 * Obtains the SFK. If Sfk is null, it uses the Sfk of Owner's delegations.
	 *
	 * @param delegations
	 * @param ownerId
	 * @param ownerPrivateKey
	 * @return The UTF8 String SFK
	 */
	public String obtainSfk(Map<String, List<DelegationDto>> delegations, String ownerId, PrivateKey ownerPrivateKey) throws ICureException, IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		/**
		 * Since we need to add the same Secret Foreign Key(SFK) for this new delegation. We need to do followings:
		 * 1. fetch the delegation in which the current owner is delegate.
		 * 2. decrypt its Key field of the delegation and obtain the SFK
		 * 3. the using this SFK ws should be able to create the delegation
		 */

		if (delegations.get(ownerId) == null) {
			throw new ICureException("The HcParty (" + ownerId +") is not allowed to delegate this entity. No delegation for him/her.");
		}
		DelegationDto d = delegations.get(ownerId).get(0);
		byte[] exKey = getDelegateHcPartyKey(d.getDelegatedTo(), d.getOwner(), ownerPrivateKey);
		String previousSfk =new String(
				CryptoUtils.decryptAES(decodeHex(d.getKey()), exKey)
				,"UTF8"
		);

		return previousSfk.split (":") [1];
	}

	public DelegationDto createDelegation(Map<String, List<DelegationDto>> delegations, String objectId, String ownerId, String delegateId, String sfk, boolean isInitDelegation) throws ICureException, InvalidKeyException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, ExecutionException {
		PrivateKey ownerPrivateKey =  RSAKeysUtils.loadMyKeyPair(ownerId).getPrivate();
		if (sfk == null && !isInitDelegation) {
			sfk = obtainSfk (delegations, ownerId, ownerPrivateKey);
		}

		byte[] exchangeAESKeyOwnerDelegate = getOwnerHcPartyKey (ownerId, delegateId, ownerPrivateKey);

		return newDelegation (ownerId, delegateId, objectId, isInitDelegation ? null : sfk , exchangeAESKeyOwnerDelegate);
	}

	/**
	 * This method returns a hcPartyKey, in which the myId is a delegate, and the ownerId is the owner.
	 *
	 * @param myId
	 * @param ownerId
	 * @param privateKey
	 * @return
	 * @throws IOException
	 * @throws EncryptionException
	 */
    public byte[] getDelegateHcPartyKey(String myId, String ownerId, PrivateKey privateKey) throws IOException, EncryptionException {
        Map<String, EncryptedCryptedKey> keyMap;
        try {
            keyMap = hcDelegatePartyKeysCache.get(myId, () -> {
                String response = doRestGET("hcparty/"+myId+"/keys");
                Map<String, EncryptedCryptedKey> result = new HashMap<>();
                for (Map.Entry<String, String> entry : getGson().<Map<String, String>>fromJson(response, Map.class).entrySet()) {
                    result.put(entry.getKey(), new EncryptedCryptedKey(entry.getValue(), null));
                }
                return result;
            });
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }

        EncryptedCryptedKey k = keyMap.get(ownerId);

        return k.getDecrypted(privateKey);
    }

	public byte[] getOwnerHcPartyKey(String myId, String delegateId, PrivateKey privateKey) throws IOException, EncryptionException, ExecutionException, NoSuchProviderException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        Map<String, EncryptedCryptedKey> keyMap = hcOwnerPartyKeysCache.get(myId, () -> {
            String response = doRestGET("hcparty/" + myId);
            HealthcarePartyDto hcp = getGson().fromJson(response, HealthcarePartyDto.class);
            Map<String, EncryptedCryptedKey> result = new HashMap<>();
            for (Map.Entry<String, String[]> entry : hcp.getHcPartyKeys().entrySet()) {
                result.put(entry.getKey(), new EncryptedCryptedKey(entry.getValue()[0], null));
            }
            return result;
        });

        EncryptedCryptedKey k = keyMap.get(delegateId);
        if (k==null) {
			HealthcarePartyDto delegateHcParty = getHealthcarePartyHelper().get(delegateId);
			String delegatePublicKey = delegateHcParty.getPublicKey();

			PublicKey myPublicKey = RSAKeysUtils.loadMyKeyPair(myId).getPublic();

			// generate exchange key (plain)
			Key exchangeAESKey = CryptoUtils.generateKeyAES();

			// crypting with delegate HcParty public key
			String delegateCryptedKey = ICureHelper.encodeHex(
                    CryptoUtils.encrypt(
                            exchangeAESKey.getEncoded(),
                            RSAKeysUtils.toPublicKey(delegatePublicKey)
                    )
            ).toString();

			// crypting with my public key (i.e. owner)
			String myCryptedKey = ICureHelper.encodeHex(
                    CryptoUtils.encrypt(
                            exchangeAESKey.getEncoded(),
                            myPublicKey
                    )
            ).toString();

			// update the owner (myself) through REST
			String responseHcPartyKeysUpdate = doRestPUT("hcparty/keys", Collections.singletonMap(delegateId, new String[]{myCryptedKey, delegateCryptedKey}));
			// It has to be Map<String, String[]>. But weirdly gson convert it to ArrayList
			Map<String, ArrayList<String>> newKeyMap = getGson().<Map<String, ArrayList<String>>>fromJson(responseHcPartyKeysUpdate, Map.class);

			// update the caches, delegate and owner
			Map<String, EncryptedCryptedKey> existingKeyMapOfOwner = hcOwnerPartyKeysCache.getIfPresent(myId);
			if (existingKeyMapOfOwner == null ) { hcOwnerPartyKeysCache.put(myId, existingKeyMapOfOwner = new HashMap<>()); }
			existingKeyMapOfOwner.put(delegateId, new EncryptedCryptedKey(newKeyMap.get(delegateId).get(0), null));

			Map<String, EncryptedCryptedKey> existingKeyMapOfDelegate = hcDelegatePartyKeysCache.getIfPresent(delegateId);
			if (existingKeyMapOfDelegate == null ) { hcDelegatePartyKeysCache.put(delegateId, existingKeyMapOfDelegate = new HashMap<>()); }
			existingKeyMapOfDelegate.put(myId, new EncryptedCryptedKey(newKeyMap.get(delegateId).get(1) ,null));

			k = new EncryptedCryptedKey(myCryptedKey, exchangeAESKey.getEncoded());
		}
        return k.getDecrypted(privateKey);
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
    public <T extends IcureDto> T initObjectDelegations(T createdObject, IcureDto parentObject, String ownerId, String newDelegationCreatedObjectPostMethod, String modificationParentObjectPostMethod) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        PrivateKey ownerPrivateKey = RSAKeysUtils.loadMyKeyPair(ownerId).getPrivate();

        //Initial self delegation to store a randomly secure secret key generated inside newDelegation
		DelegationDto selfDelegation = newDelegation(ownerId, ownerId, createdObject.getId(), null, ownerPrivateKey);

		// optimization - if the facade of (newDelegationCreatedObjectPostMethod == modificationParentObjectPostMethod)
		boolean isSameFacade = false;
		if (modificationParentObjectPostMethod != null) {
			String[] splitModificationParentObjectPostMethod = modificationParentObjectPostMethod.split("/");
			String[] splitNewDelegationCreatedObjectPostMethod = newDelegationCreatedObjectPostMethod.split("/");
			isSameFacade = splitModificationParentObjectPostMethod.length>0 && splitModificationParentObjectPostMethod.length>0 && splitModificationParentObjectPostMethod[0].equals(splitNewDelegationCreatedObjectPostMethod[0]);
		}

		// call the server for new delegation, if (isSameFace) -> no server call at this point - optimization
		T createdObjectWithSelfDelegation;
		if(isSameFacade){
			// no server call (deferred)
			createdObject.addDelegation(selfDelegation.getDelegatedTo(), selfDelegation);
			createdObjectWithSelfDelegation = createdObject;
		} else {
			// with server call
			String responseCreatedDelegation = doRestPOST(newDelegationCreatedObjectPostMethod.replaceAll("\\{id\\}",createdObject.getId()), selfDelegation);
			createdObjectWithSelfDelegation = getGson().<T>fromJson(responseCreatedDelegation, createdObject.getClass());
		}

        if (parentObject!=null) {
            DelegationDto delegationForCryptedForeignKey = newDelegation(ownerId, ownerId, createdObject.getId(), parentObject.getId(), ownerPrivateKey);
			createdObjectWithSelfDelegation.addCryptedForeignKeys(ownerId, delegationForCryptedForeignKey);

            Set<String> secureKeys = getSecureKeys(parentObject.getDelegations(), ownerId, ownerPrivateKey);
			createdObjectWithSelfDelegation.setSecretForeignKeys(secureKeys);

			// save the modifications
			String responseModifiedObject = doRestPOST(modificationParentObjectPostMethod, createdObjectWithSelfDelegation);
			return getGson().<T>fromJson(responseModifiedObject, createdObject.getClass());
		}

		return createdObjectWithSelfDelegation;
    }

	/**
	 *
	 * @param newDelegationModifiedObjectPostMethod this can be for example "contact/delegate/"
	 * @param modificationModifiedObjectPostMethod this can be for example "contact/modify"
	 * @param newDelegationParentObjectPostMethod  this can be for example "patient/delegate/"
	 * @throws EncryptionException
	 * @throws ExecutionException
	 * @throws IOException
	 */
    public <T extends IcureDto> T appendObjectDelegations(T modifiedObject, T parentObject, String ownerId, String delegateId ,String newDelegationModifiedObjectPostMethod, String modificationModifiedObjectPostMethod, String newDelegationParentObjectPostMethod) throws ICureException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        PrivateKey ownerPrivateKey = RSAKeysUtils.loadMyKeyPair(ownerId).getPrivate();

    /*    // Fetch exchange AES key from cache, and decrypt the owner delegation
		// SFK to obtain the previously generated key of the modifiedObject.
		byte[] exchangeAESKeyOwnerOwner= getOwnerHcPartyKey(ownerId, ownerId, ownerPrivateKey);
		String decryptedSFK = decryptSFKInDelegate(ownerCryptedDelegationSFK, exchangeAESKeyOwnerOwner);
		byte[] exchangeAESKeyOwnerDelegate = getOwnerHcPartyKey(ownerId, delegateId, ownerPrivateKey);
		DelegationDto newDelegation = newDelegation(ownerId, delegateId, decryptedSFK, exchangeAESKeyOwnerDelegate);*/

		DelegationDto newDelegation = createDelegation(modifiedObject.getDelegations(),modifiedObject.getId(), ownerId, delegateId, null, false);

		// optimization - if the facade of (newDelegationCreatedObjectPostMethod == modificationParentObjectPostMethod)
		boolean isSameFacade = false;
		if (modificationModifiedObjectPostMethod != null) {
			String[] splitModificationModifiedObjectPostMethod = modificationModifiedObjectPostMethod.split("/");
			String[] splitNewDelegationModifiedObjectPostMethod = newDelegationModifiedObjectPostMethod.split("/");
			isSameFacade = splitModificationModifiedObjectPostMethod.length>0 && splitModificationModifiedObjectPostMethod.length>0 && splitModificationModifiedObjectPostMethod[0].equals(splitNewDelegationModifiedObjectPostMethod[0]);
		}

		// call the server for new delegation, if (isSameFace) -> no server call at this point - optimization
		T modifiedObjectWithNewDelegation;
		if(isSameFacade){
			// no server call (deferred)
			modifiedObject.addDelegation(newDelegation.getDelegatedTo(), newDelegation);
			modifiedObjectWithNewDelegation = modifiedObject;
		} else {
			String responseCreatedDelegation = doRestPOST(newDelegationModifiedObjectPostMethod + modifiedObject.getId(), newDelegation);
			modifiedObjectWithNewDelegation = getGson().<T>fromJson(responseCreatedDelegation, modifiedObject.getClass());
		}

        if (parentObject!=null) {
			/* adding new delegation in parent object with new key(SFK)*/
			String newSFK = idg.newGUID().toString();
//			DelegationDto newOwnerDelegateDelegationForParentObject = newDelegation(ownerId, delegateId, parentObject.getId(), newSFK, exchangeAESKeyOwnerDelegate);
			DelegationDto newOwnerDelegateDelegationForParentObject = createDelegation(parentObject.getDelegations(), parentObject.getId(), ownerId, delegateId, newSFK, false);
			String responseParentDelegation = doRestPOST(newDelegationParentObjectPostMethod + parentObject.getId(), newOwnerDelegateDelegationForParentObject);

			/* Adding CryptedForeignKeys and SecretForeignKeys */
            DelegationDto delegationForCryptedForeignKey = newDelegation(ownerId, delegateId, modifiedObject.getId(), parentObject.getId(), ownerPrivateKey);
			modifiedObjectWithNewDelegation.addCryptedForeignKeys(delegateId, delegationForCryptedForeignKey);

			modifiedObjectWithNewDelegation.addSecretForeignKey(newSFK);

			// save the modifications
			String responseModifiedObject = doRestPOST(modificationModifiedObjectPostMethod, modifiedObjectWithNewDelegation);
			return getGson().<T>fromJson(responseModifiedObject, modifiedObject.getClass());
        }

		return modifiedObjectWithNewDelegation;
    }

	/**
	 *
	 * @param delegateId, we are delegate HcParty here. (myId)
	 * @return secret generated keys located in patient delegation. Those which are delegated to delegate HcParty
	 */
	public <T extends IcureDto> List<String> getSecretKeys(T childObject, String ownerId, String delegateId) throws IOException, EncryptionException {
		// Fetching AES exchange key of owner and delagate from cache (or remote server)
		byte[] delegateOwnerAesExchangeKey = getDelegateHcPartyKey(delegateId, ownerId, RSAKeysUtils.loadMyKeyPair(delegateId).getPrivate());

		/* TODO:
		  * The return List is redundant. Because there is always one delegation for (OwnerId,delegateId).
		  * This should be removed and corrected everywhere.
		*/
		List<String> results = new ArrayList<>();
		for (DelegationDto delegation : childObject.getDelegations().get(delegateId)) {
			if (ownerId.equals(delegation.getOwner())) {
				String plainSFK = decryptSFKInDelegate(delegation.getKey(), delegateOwnerAesExchangeKey);

				// plainSFK => childObjectId:secretKey
				results.add(plainSFK.split(":")[1]);
			}
		}

		return results;
	}

	private class EncryptedCryptedKey {
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


	public AuthenticationResponse login() throws IOException {
		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", username);
		credentials.put("password", password);


		String response = doRestPOST("auth/login", credentials);
		return getGson().fromJson(response, AuthenticationResponse.class);
	}

	public AuthenticationResponse logout() throws IOException {
		String response = doRestGET("auth/logout");

		return getGson().fromJson(response, AuthenticationResponse.class);
	}


}

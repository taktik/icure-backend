'use strict';

/**
 * @ngdoc service
 * @name doctorApp.Cryptoservice
 * @description
 * # Cryptoservice
 * Service in the doctorApp.
 */
angular.module('doctorApp')
	.service('Cryptoservice', ['$resource', 'localStorageService', function ($resource, localStorageService) {

		var thisService = this;

		/*
		 [KeyFormat]
		 Specifies a serialization format for a key. The recognized key format values are:
		 - raw:     An unformatted sequence of bytes. Intended for secret keys.
		 - pkcs8:   The DER encoding of the PrivateKeyInfo structure from RFC 5208.
		 - spki:    The DER encoding of the SubjectPublicKeyInfo structure from RFC 5280.
		 - jwk:     The key is represented as JSON according to the JSON Web Key format.
		 */

		this.AES = {};
		this.RSA = {};
		this.utils = {};

		/********* AES Config **********/
		// Random array as IV (16)
		//TODO generate new one and concatenated to the enc
		//TODO twice encryption of a thing have to be different with different IV's
		//
		//var iv = new Uint8Array([12, 127, 42, 84, 35, 36, 45, 36, 121, 89, 56, 117, 49, 64, 23, 92]);
		var ivLenght = 16;
		var aesAlgorithmEncrypt = {
			name: 'AES-CBC',
			iv: null // will be generated on the fly before each encryption
		};
		var aesKeyGenParams = {
			name: 'AES-CBC',
			length: 256
		};
		var aesLocalStoreIdPrefix = 'org.taktik.icure.aes.';

		/********* RSA Config **********/
		//TODO bigger modulus
		//TODO check the randomness of the implementations. Normally RSA must have some notions of randomness. This might be done through WebCrypto source codes
		//TODO PSS for signing
		var rsaParams = { name: 'RSA-OAEP' };
		// RSA params for 'import' and 'generate' function.
		var rsaHashedParams = {
			name: 'RSA-OAEP',
			modulusLength: 2048,
			publicExponent: new Uint8Array([0x01, 0x00, 0x01]),  // Equivalent to 65537 (Fermat F4), read http://en.wikipedia.org/wiki/65537_(number)
			hash: {name: 'sha-1'}
		};
		var rsaLocalStoreIdPrefix = 'org.taktik.icure.rsa.';

		/* AES encryption */

    this.AES.encrypt = function (cryptoKey, plainData) {
	    return new Promise(function (resolve, reject) {
		    aesAlgorithmEncrypt.iv = thisService.AES.generateIV(ivLenght);
		    window.crypto.subtle.encrypt(aesAlgorithmEncrypt, cryptoKey, plainData).then(
			    function (cipherData) {
				    resolve(thisService.utils.appendBuffer(aesAlgorithmEncrypt.iv, cipherData));
			    },
			    function (err) {
				    reject('AES encryption failed: ', err);
			    }
		    );
	    });
    };

		/**
		 *
		 * @param cryptoKey (CryptoKey)
		 * @param encryptedData (ArrayBuffer)
		 * @returns {Promise} will be ArrayBuffer
		 */
		this.AES.decrypt = function (cryptoKey, encryptedData) {
			var encryptedDataUnit8 = new Uint8Array(encryptedData);
			aesAlgorithmEncrypt.iv = encryptedDataUnit8.subarray(0, ivLenght);
			/*
			 * IF THIS BIT OF CODE PRODUCES A DOMEXCEPTION CODE 0 ERROR, IT MIGHT BE RELATED TO THIS:
			 *
			 * NOTOK:
			 * if (!hcparty.hcPartyKeys && !hcparty.hcPartyKeys[hcpartyId] && hcparty.hcPartyKeys[hcpartyId].length !== 2) {
			 *   throw 'No hcPartyKey for this Healthcare party(' + hcpartyId + ').';
			 * }
			 * var delegateHcPartyKey = hcparty.hcPartyKeys[hcpartyId][1];
			 *
			 * SHOULD BE:
			 * var delegatorId = patient.delegations[hcpartyId][0].owner;
			 * if (!hcparty.hcPartyKeys && !hcparty.hcPartyKeys[delegatorId] && hcparty.hcPartyKeys[delegatorId].length !== 2) {
			 *   throw 'No hcPartyKey for this Healthcare party(' + delegatorId + ').';
			 * }
			 * var delegateHcPartyKey = hcparty.hcPartyKeys[delegatorId][1];
			 */
			return window.crypto.subtle.decrypt(aesAlgorithmEncrypt, cryptoKey, encryptedDataUnit8.subarray(ivLenght, encryptedDataUnit8.length));
		};

		// generate a 1024-bit RSA key pair for encryption
		/**
		 *
		 * @param toHex boolean, if true, it returns hex String
		 * @returns {Promise} either Hex string or CryptoKey
		 */
		this.AES.generateCryptoKey = function (toHex) {
			if (toHex === undefined || !toHex) {
				var extractable = true;
				var keyUsages = ['decrypt', 'encrypt'];
				return window.crypto.subtle.generateKey(aesKeyGenParams, extractable, keyUsages);
			} else {
				return new Promise(function (resolve) {
					var extractable = true;
					var keyUsages = ['decrypt', 'encrypt'];
					window.crypto.subtle.generateKey(aesKeyGenParams, extractable, keyUsages).then(
						function (k) {
							return thisService.AES.exportKey(k, 'raw');
						},
						function (err) {
							console.log('Error in generateKey: ' + err);
						}
					).then(
						function (rawK) {
							resolve(thisService.utils.ua2hex(rawK));
						},
						function (err) {
							new Error(err);
						}
					);
				});
			}
		};

		this.AES.generateIV = function(ivByteLenght) {
			return window.crypto.getRandomValues(new Uint8Array(ivByteLenght));
		};

		/**
		 * This function return a promise which will be the key Format will be either 'raw' or 'jwk'.
		 * JWK: Json Web key (ref. http://tools.ietf.org/html/draft-ietf-jose-json-web-key-11)
		 *
		 * @param cryptoKey CryptoKey
		 * @param format will be 'raw' or 'jwk'
		 * @returns {Promise} will the AES Key
		 */
		this.AES.exportKey = function (cryptoKey, format) {
			return window.crypto.subtle.exportKey(format, cryptoKey);
		};

		/**
		 * the ability to import a key that have already been created elsewhere, for use within the web
		 * application that is invoking the import function, for use within the importing web application's
		 * origin. This necessiates an interoperable key format, such as JSON Web Key [JWK] which may be
		 * represented as octets.
		 *
		 * https://chromium.googlesource.com/chromium/blink.git/+/6b902997e3ca0384c8fa6fe56f79ecd7589d3ca6/LayoutTests/crypto/resources/common.js
		 *
		 * @param format 'raw' or 'jwk'
		 * @param aesKey
		 * @returns {*}
		 */
		this.AES.importKey = function (format, aesKey) {
			//TODO test
			var extractable = true;
			var keyUsages = ['decrypt', 'encrypt'];
			return window.crypto.subtle.importKey(format, aesKey, aesKeyGenParams, extractable, keyUsages);
		};

		/**
		 *
		 * @param id
		 * @param key should be JWK
		 */
		this.AES.storeKeyPair = function (id, key) {
			if (!localStorageService.isSupported) {
				console.log('Your browser does not support HTML5 Browser Local Storage !');
				throw 'Your browser does not support HTML5 Browser Local Storage !';
			}

			//TODO encryption
			localStorageService.set(aesLocalStoreIdPrefix + id, key);
		};

		this.AES.loadKeyPairNotImported = function (id) {
			if (!localStorageService.isSupported) {
				console.log('Your browser does not support HTML5 Browser Local Storage !');
				throw 'Your browser does not support HTML5 Browser Local Storage !';
			}

			//TODO decryption
			return localStorageService.get(aesLocalStoreIdPrefix + id);
		};

		/* RSA encryption */

		/**
		 * It returns CryptoKey promise, which doesn't hold the bytes of the key.
		 * If bytes are needed, you must export the generated key.
		 * R
		 * @returns {Promise} will be {publicKey: CryptoKey, privateKey: CryptoKey}
		 */
		this.RSA.generateKeyPair = function () {
			var extractable = true;
			var keyUsages = ['decrypt', 'encrypt'];
			return window.crypto.subtle.generateKey(rsaHashedParams, extractable, keyUsages);
		};

		/**
		 *
		 * 'JWK': Json Web key (ref. http://tools.ietf.org/html/draft-ietf-jose-json-web-key-11)
		 * 'spki': for private key
		 * 'pkcs8': for private Key
		 *
		 * @param keyPair is {publicKey: CryptoKey, privateKey: CryptoKey}
		 * @param privKeyFormat will be 'pkcs8' or 'jwk'
		 * @param pubKeyFormat will be 'spki' or 'jwk'
		 * @returns {Promise} will the AES Key
		 */
		this.RSA.exportKeys = function (keyPair, privKeyFormat, pubKeyFormat) {
			var pubPromise = window.crypto.subtle.exportKey(pubKeyFormat, keyPair.publicKey);
			var privPromise = window.crypto.subtle.exportKey(privKeyFormat, keyPair.privateKey);

			return Promise.all([pubPromise, privPromise]).then(function (results) {
				return {
					publicKey: results[0],
					privateKey: results[1]
				};
			});
		};

		/**
		 *  Format:
		 *
		 * 'JWK': Json Web key (ref. http://tools.ietf.org/html/draft-ietf-jose-json-web-key-11)
		 * 'spki': for private key
		 * 'pkcs8': for private Key
		 *
		 * @param cryptoKey public or private
		 * @param format either 'jwk' or 'spki' or 'pkcs8'
		 * @returns {Promise|*} will be RSA key (public or private)
		 */
		this.RSA.exportKey = function (cryptoKey, format) {
			return window.crypto.subtle.exportKey(format, cryptoKey);
		};

		/**
		 *
		 * @param publicKey (CryptoKey)
		 * @param plainData (Uint8Array)
		 */
		this.RSA.encrypt = function (publicKey, plainData) {
			return window.crypto.subtle.encrypt(rsaParams, publicKey, plainData);
		};

		this.RSA.decrypt = function (privateKey, encryptedData) {
			return window.crypto.subtle.decrypt(rsaParams, privateKey, encryptedData);
		};

		/**
		 *
		 * @param format 'jwk', 'spki', or 'pkcs8'
		 * @param keydata should be the key data based on the format.
		 * @param keyUsages Array of usages. For example, ['encrypt'] for public key.
		 * @returns {*}
		 */
		this.RSA.importKey = function (format, keydata, keyUsages) {
			var extractable = true;
			return window.crypto.subtle.importKey(format, keydata, rsaHashedParams, extractable, keyUsages);
		};

		/**
		 *
		 * @param format 'jwk' or 'pkcs8'
		 * @param keydata should be the key data based on the format.
		 * @returns {*}
		 */
		this.RSA.importPrivateKey = function (format, keydata) {
			var extractable = true;
			return window.crypto.subtle.importKey(format, keydata, rsaHashedParams, extractable, ['decrypt']);
		};

		/**
		 *
		 * @param privateKeyFormat 'jwk' or 'pkcs8'
		 * @param privateKeydata    should be the key data based on the format.
		 * @param publicKeyFormat 'jwk' or 'spki'
		 * @param publicKeyData should be the key data based on the format.
		 * @returns {Promise|*}
		 */
		this.RSA.importKeyPair = function (privateKeyFormat, privateKeydata, publicKeyFormat, publicKeyData) {
			var extractable = true;
			var privPromise = window.crypto.subtle.importKey(privateKeyFormat, privateKeydata, rsaHashedParams, extractable, ['decrypt']);
			var pubPromise = window.crypto.subtle.importKey(publicKeyFormat, publicKeyData, rsaHashedParams, extractable, ['encrypt']);

			return Promise.all([pubPromise, privPromise]).then(function (results) {
				return {
					publicKey: results[0],
					privateKey: results[1]
				};
			});
		};

		/**
		 *
		 * @param id
		 * @param keyPair should be JWK
		 */
		this.RSA.storeKeyPair = function (id, keyPair) {
			if (!localStorageService.isSupported) {
				console.log('Your browser does not support HTML5 Browser Local Storage !');
				throw 'Your browser does not support HTML5 Browser Local Storage !';
			}
			//TODO encryption
			localStorageService.set(rsaLocalStoreIdPrefix + id, keyPair);
		};

		/**
		 * loads the RSA key pair (hcparty) in JWK, not importet
		 *
		 * @param id  doc id - hcpartyId
		 * @returns {Object} it is in JWK - not imported
		 */
		this.RSA.loadKeyPairNotImported = function (id) {
			if (!localStorageService.isSupported) {
				console.log('Your browser does not support HTML5 Browser Local Storage !');
				throw 'Your browser does not support HTML5 Browser Local Storage !';
			}
			//TODO decryption
			return localStorageService.get(rsaLocalStoreIdPrefix + id);
		};

		/**
		 * Loads and imports the RSA key pair (hcparty)
		 *
		 * @param id  doc id - hcPartyId
		 * @returns {Promise} -> {CryptoKey} - imported RSA
		 */
		this.RSA.loadKeyPairImported = function (id) {
			// TODO test
			return new Promise(function (resolve, reject) {
				try {
					var jwkKeyPair = localStorageService.get(rsaLocalStoreIdPrefix + id);
					thisService.RSA.importKeyPair('jwk', jwkKeyPair.privateKey, 'jwk', jwkKeyPair.publicKey).then(
						function (keyPair) {
							resolve(keyPair);
						},
						function (err) {
							console.log('Error in RSA.importKeyPair: ' + err);
							reject(new Error(err));
						}
					);
				}
				catch (err) {
					reject(new Error(err));
				}
			});
		};

		/* Utilities */

		/**
		 * String to Uint8Array
		 *
		 * @param s
		 * @returns {Uint8Array}
		 */
		this.utils.text2ua = function (s) {
			var ua = new Uint8Array(s.length);
			for (var i = 0; i < s.length; i++) {
				ua[i] = s.charCodeAt(i);
			}
			return ua;
		};

		/**
		 * Hex String to Uint8Array
		 *
		 * @param s
		 * @returns {Uint8Array}
		 */
		this.utils.hex2ua = function (s) {
			var ua = new Uint8Array(s.length / 2);
			s = s.toLowerCase();
			for (var i = 0; i < s.length; i += 2) {
				ua[i / 2] = (s.charCodeAt(i) < 58 ? s.charCodeAt(i) - 48 : s.charCodeAt(i) - 87) * 16 + (s.charCodeAt(i + 1) < 58 ? s.charCodeAt(i + 1) - 48 : s.charCodeAt(i + 1) - 87);
			}
			return ua;
		};

		/**
		 * Uint8Array/ArrayBuffer to hex String
		 *
		 * @param ua {Uint8Array} or ArrayBuffer
		 * @returns {String} Hex String
		 */
		this.utils.ua2hex = function (ua) {
			var s = '';
			ua = (ua instanceof Uint8Array) ? ua : new Uint8Array(ua);
			for (var i = 0; i < ua.length; i++) {
				var hhb = (ua[i] & 0xF0) >> 4;
				var lhb = (ua[i] & 0x0F);
				s += String.fromCharCode(hhb > 9 ? hhb + 87 : hhb + 48);
				s += String.fromCharCode(lhb > 9 ? lhb + 87 : lhb + 48);
			}
			return s;
		};

		/**
		 * ArrayBuffer to String - resilient to large ArrayBuffers.
		 *
		 * @param arrBuf
		 * @returns {string}
		 */
		this.utils.ua2text = function (arrBuf) {
			var str = '';
			var ab = new Uint8Array(arrBuf);
			var abLen = ab.length;
			var CHUNK_SIZE = Math.pow(2, 8);
			var offset, len, subab;
			for (offset = 0; offset < abLen; offset += CHUNK_SIZE) {
				len = Math.min(CHUNK_SIZE, abLen - offset);
				subab = ab.subarray(offset, offset + len);
				str += String.fromCharCode.apply(null, subab);
			}
			return str;
		};

		this.utils.hex2text = function (hexStr) {
			return thisService.utils.ua2text(thisService.utils.hex2ua(hexStr));
		};

		this.utils.text2hex = function (text) {
			return thisService.utils.ua2hex(thisService.utils.text2ua(text));
		};

		this.utils.asciiToArrayBuffer = function (str) {
			var chars = [];
			for (var i = 0; i < str.byte; ++i) {
				chars.push(str.charCodeAt(i));
			}
			return new Uint8Array(chars);
		};

		/**
		 * Builds a hex string representation of any array-like input (array or
		 * ArrayBufferView). The output looks like this:
		 *  [ab 03 4c 99]
		 *
		 * @param bytes
		 * @returns {string}
		 */
		this.utils.byteArrayToHexString = function (bytes) {
			var hexBytes = [];
			for (var i = 0; i < bytes.length; ++i) {
				var byteString = bytes[i].toString(16);
				if (byteString.length < 2) {
					byteString = '0' + byteString;
				}
				hexBytes.push(byteString);
			}
			return '[' + hexBytes.join(' ') + ']';
		};


		/**
		 *
		 * @param buffer1 {Uint8Array}
		 * @param buffer2{ Uint8Array}
		 * @returns {ArrayBuffer}
		 */
		this.utils.appendBuffer = function( buffer1, buffer2 ) {
			var tmp = new Uint8Array( buffer1.byteLength + buffer2.byteLength );
			tmp.set( new Uint8Array( buffer1 ), 0 );
			tmp.set( new Uint8Array( buffer2 ), buffer1.byteLength );
			return tmp.buffer;
		};
	}]);

'use strict';

angular.module('doctorApp').factory('HealthcarePartyKeys', function ($q, CacheProvider, Cryptoservice, Hcparties) {

	return {
		/**
		 * Decrypts and imports all the AES keys inside an array of delegations that are related to a given healthcare
		 * party.
		 * The keys are then imported in the browser for future usage.
		 */
		decryptAndImportAesHcPartyKeysInDelegations: function(healthcarePartyId, delegations) {
			var result = $q.defer();

			Hcparties.getHcPartyKeysForDelegate(healthcarePartyId).then(function (healthcarePartyKeys) {

				var delegatorIds = {};
				delegations[healthcarePartyId].forEach(function (delegation) {
					delegatorIds[delegation.owner] = true;
				});
				var delegatorIdsSet = Object.keys(delegatorIds);

				var aesDecryptedAndImportedHcPartyKeysPromises = [];

				// For each delegatorId, obtain the AES keys
				delegatorIdsSet.forEach(function (delegatorId) {

					var encryptedHcPartyKey = healthcarePartyKeys[delegatorId];

					var keyPair = CacheProvider.rsaKeyPairs.get(healthcarePartyId);
					if (!keyPair) {

						var keyPairInJwk = Cryptoservice.RSA.loadKeyPairNotImported(healthcarePartyId);
						if (!keyPairInJwk) {
							throw 'No RSA private key for Healthcare party(' + healthcarePartyId + ').';
						}

						// import the jwk formatted key
						aesDecryptedAndImportedHcPartyKeysPromises.push(Cryptoservice.RSA.importKeyPair('jwk', keyPairInJwk.privateKey, 'jwk', keyPairInJwk.publicKey).then(function (importedKeyPair) {
							keyPair = importedKeyPair;
							// Obtaining the AES Key by decrypting the HcpartyKey
							return Cryptoservice.RSA.decrypt(keyPair.privateKey, Cryptoservice.utils.hex2ua(encryptedHcPartyKey));

						}).then(function (decryptedHcPartyKey) {
							// Decrypt encryptedHcPartyKey
							return Cryptoservice.AES.importKey('raw', decryptedHcPartyKey);
						}, function (err) {
							console.error(err);

						}).then(function (decryptedImportedHcPartyKey) {
							return {delegatorId: delegatorId, key: decryptedImportedHcPartyKey};
						}, function (err) {
							console.error(err);
						}));

					} else {

						aesDecryptedAndImportedHcPartyKeysPromises.push(Cryptoservice.RSA.decrypt(keyPair.privateKey, Cryptoservice.utils.hex2ua(encryptedHcPartyKey)).then(function (decryptedHcPartyKey) {
							return Cryptoservice.AES.importKey('raw', decryptedHcPartyKey);
						}, function (err) {
							console.error(err);

						})).then(function (decryptedImportedHcPartyKey) {
							return {delegatorId: delegatorId, key: decryptedImportedHcPartyKey}
						}, function (err) {
							console.error(err);
						});
					}
				});

				$q.all(aesDecryptedAndImportedHcPartyKeysPromises).then(function (decryptedAndImportedAesHcPartyKeys) {
					result.resolve(decryptedAndImportedAesHcPartyKeys);

				}, function (err) {
					console.error(err);
					result.reject(err);
				});

			}).catch(function (err) {
				console.error(err);
				result.reject(err);
			});

			return result.promise;
		}
	};
});
'use strict';

angular.module('doctorApp').factory('SecretForeignKeys', function ($q, Cryptoservice) {

	return {
		/**
		 * Decrypts a patient's delegations' secret foreign keys using the AES keys from the specified array, for a
		 * patient with the specified ID.
		 * @param delegationsArray An array of delegation objects having "delegatedTo", "key" and "owner" properties.
		 * @param aesKeys A map of CryptoKey objects indexed by their owner's id.
		 * @param patientId The ID of the patient whose delegations' SFKs we need to decrypt.
		 * @returns {*} A promise that will eventually resolve to an array of decrypted SFKs.
		 */
		decryptPatientDelegationsSFKs: function(delegationsArray, aesKeys, patientId) {
			var result = $q.defer();

			var decryptPromises = [];
			for (var i = 0; i < delegationsArray.length; i++) {
				var delegation = delegationsArray[i];

				decryptPromises.push(Cryptoservice.AES.decrypt(aesKeys[delegation.owner], Cryptoservice.utils.hex2ua(delegation.key)).then(function (result) {
					var results = Cryptoservice.utils.ua2text(result).split(':');
					// results[0]: must be the ID of patient, for checksum
					// results[1]: secretForeignKey
					if (results[0] !== patientId && results[0] !== 'MockID') {
						throw 'Cryptographic mistake: patient ID is not equal to the concatenated id in SecretForeignKey';
					}

					return results[1];

				}, function (err) {
					console.error(err);
				}));
			}

			$q.all(decryptPromises).then(function (secretForeignKeys) {
				result.resolve(secretForeignKeys);
			}).catch(function(response) {
				result.reject(response);
			});

			return result.promise;
		}
	};
});
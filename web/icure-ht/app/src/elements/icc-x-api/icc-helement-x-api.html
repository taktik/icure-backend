	<link rel="import" href="../../../bower_components/polymer/polymer.html">

	<dom-module id="icc-helement-x-api">
		<template>
			<style>
			</style>
		</template>
	</dom-module>

	<script>
import * as models from '../icc-api/model/models';
import moment from '../../../bower_components/moment/src/moment';

class IccHelementXApi extends Polymer.mixinBehaviors([], Polymer.Element) {
	static get is() {
		return 'icc-helement-x-api';
	}

	static get properties() {
		return {
			api: {
				type: Object
			},
			crypto: {
				type: Object
			}
		};
	}

	constructor() {
		super();
	}

	init() {
		this.baseApi = this.api.helement();
		const proto = Object.getPrototypeOf(this.baseApi);
		Object.getOwnPropertyNames(proto).forEach(p => {
			if (p !== 'constructor' && p !== 'handleError' && proto[p] && typeof proto[p] === 'function') {
				this[p] = this.baseApi[p].bind(this.baseApi);
			}
		});
	}

	newInstance(user, patient, h) {
		const helement = _.assign({
			id: this.crypto.randomUuid(),
			_type: 'org.taktik.icure.entities.HealthElement',
			created: new Date().getTime(),
			modified: new Date().getTime(),
			responsible: user.healthcarePartyId,
			author: user.id,
			codes: [],
			tags: [],
			healthElementId: this.crypto.randomUuid(),
			openingDate: parseInt(moment().format('YYYYMMDDHHmmss'))
		}, h || {});

		return this.crypto.extractDelegationsSFKs(patient, user.healthcarePartyId).then(secretForeignKeys => this.crypto.initObjectDelegations(helement, patient, user.healthcarePartyId, secretForeignKeys[0])).then(initData => {
			_.extend(helement, { delegations: initData.delegations, cryptedForeignKeys: initData.cryptedForeignKeys, secretForeignKeys: initData.secretForeignKeys });

			let promise = Promise.resolve(helement);
			(user.autoDelegations ? (user.autoDelegations.all || []).concat(user.autoDelegations.medicalInformation || []) : []).forEach(delegateId => promise = promise.then(contact => this.crypto.appendObjectDelegations(contact, patient, user.healthcarePartyId, delegateId, initData.secretId)).then(extraData => _.extend(helement, { delegations: extraData.delegations, cryptedForeignKeys: extraData.cryptedForeignKeys })));
			return promise;
		});
	}

	/**
  * 1. Check whether there is a delegation with 'hcpartyId' or not.
  * 2. 'fetchHcParty[hcpartyId][1]': is encrypted AES exchange key by RSA public key of him.
  * 3. Obtain the AES exchange key, by decrypting the previous step value with hcparty private key
  *      3.1.  KeyPair should be fetch from cache (in jwk)
  *      3.2.  if it doesn't exist in the cache, it has to be loaded from Browser Local store, and then import it to WebCrypto
  * 4. Obtain the array of delegations which are delegated to his ID (hcpartyId) in this patient
  * 5. Decrypt and collect all keys (secretForeignKeys) within delegations of previous step (with obtained AES key of step 4)
  * 6. Do the REST call to get all helements with (allSecretForeignKeysDelimitedByComa, hcpartyId)
  *
  * After these painful steps, you have the helements of the patient.
  *
  * @param hcparty
  * @param patient (Promise)
  */
	findBy(hcpartyId, patient) {
		if (!patient.delegations || !patient.delegations[hcpartyId] || !(patient.delegations[hcpartyId].length > 0)) {
			throw 'There is not delegation for this healthcare party(' + hcpartyId + ') in patient(' + patient.id + ')';
		}

		return this.crypto.decryptAndImportAesHcPartyKeysInDelegations(hcpartyId, patient.delegations).then(function (decryptedAndImportedAesHcPartyKeys) {
			var collatedAesKeys = {};
			decryptedAndImportedAesHcPartyKeys.forEach(k => collatedAesKeys[k.delegatorId] = k.key);

			return this.crypto.decryptDelegationsSFKs(patient.delegations[hcpartyId], collatedAesKeys, patient.id).then(secretForeignKeys => this.api.helement().findByHCPartyPatientSecretFKeys(hcpartyId, secretForeignKeys.join(','))).then(helements => this.decrypt(hcpartyId, helements)).then(function (decryptedHelements) {
				const byIds = {};
				decryptedHelements.forEach(he => {
					if (he.healthElementId) {
						const phe = byIds[he.healthElementId];
						if (!phe || !phe.modified || he.modified && phe.modified < he.modified) {
							byIds[he.healthElementId] = he;
						}
					}
				});
				return _.values(byIds).filter(s => !s.endOfLife);
			});
		}.bind(this));
	}

	decrypt(hcpartyId, hes) {
		return Promise.all(hes.map(he => this.crypto.decryptAndImportAesHcPartyKeysInDelegations(hcpartyId, he.delegations).then(function (decryptedAndImportedAesHcPartyKeys) {
			var collatedAesKeys = {};
			decryptedAndImportedAesHcPartyKeys.forEach(k => collatedAesKeys[k.delegatorId] = k.key);
			return this.crypto.decryptDelegationsSFKs(he.delegations[hcpartyId], collatedAesKeys, he.id).then(sfks => {
				if (he.encryptedDescr) {
					return this.crypto.AES.importKey('raw', this.crypto.utils.hex2ua(sfks[0].replace(/-/g, ''))).then(key => new Promise((resolve, reject) => this.crypto.AES.decrypt(key, this.crypto.utils.text2ua(atob(he.encryptedDescr))).then(resolve).catch(err => {
						console.log("Error, could not decrypt: " + err);
						resolve(null);
					}))).then(decrypted => {
						if (decrypted) {
							he.descr = decrypted;
						}
						return he;
					});
				} else {
					return Promise.resolve(he);
				}
			});
		}.bind(this))));
	}

	serviceToHealthElement(user, patient, heSvc, language) {
		return this.newInstance(user, patient, {
			idService: heSvc.id,
			author: heSvc.author,
			responsible: heSvc.responsible,
			openingDate: heSvc.valueDate || heSvc.openingDate,
			descr: this.contactApi.shortServiceDescription(heSvc, language),
			idOpeningContact: heSvc.contactId,
			modified: heSvc.modified, created: heSvc.created,
			codes: heSvc.codes, tags: heSvc.tags
		}).then(he => {
			return this.baseApi.createHealthElement(he);
		});
	}

	stringToCode(code) {
		const c = code.split('|');
		return new models.CodeDto({ type: c[0], code: c[1], version: c[2], id: code });
	}

}

customElements.define(IccHelementXApi.is, IccHelementXApi);
</script>

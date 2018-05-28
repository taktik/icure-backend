	<link rel="import" href="../../../bower_components/polymer/polymer.html">

	<dom-module id="icc-form-x-api">
		<template>
			<style>
			</style>
		</template>
	</dom-module>

	<script>class IccFormXApi extends Polymer.mixinBehaviors([], Polymer.Element) {
	static get is() {
		return 'icc-form-x-api';
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
		this.baseApi = this.api.form();
		const proto = Object.getPrototypeOf(this.baseApi);
		Object.getOwnPropertyNames(proto).forEach(p => {
			if (p !== 'constructor' && p !== 'handleError' && proto[p] && typeof proto[p] === 'function') {
				this[p] = this.baseApi[p].bind(this.baseApi);
			}
		});
	}

	newInstance(user, patient, c) {
		const form = _.extend({
			id: this.crypto.randomUuid(),
			_type: 'org.taktik.icure.entities.Form',
			created: new Date().getTime(),
			modified: new Date().getTime(),
			responsible: user.healthcarePartyId,
			author: user.id,
			codes: [],
			tags: []
		}, c || {});

		return this.crypto.extractDelegationsSFKs(patient, user.healthcarePartyId).then(secretForeignKeys => this.crypto.initObjectDelegations(form, patient, user.healthcarePartyId, secretForeignKeys[0])).then(initData => {
			_.extend(form, { delegations: initData.delegations, cryptedForeignKeys: initData.cryptedForeignKeys, secretForeignKeys: initData.secretForeignKeys });

			let promise = Promise.resolve(form);
			(user.autoDelegations ? (user.autoDelegations.all || []).concat(user.autoDelegations.medicalInformation || []) : []).forEach(delegateId => promise = promise.then(form => this.crypto.appendObjectDelegations(form, patient, user.healthcarePartyId, delegateId, initData.secretId)).then(extraData => _.extend(form, { delegations: extraData.delegations, cryptedForeignKeys: extraData.cryptedForeignKeys })));
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
  * 6. Do the REST call to get all contacts with (allSecretForeignKeysDelimitedByComa, hcpartyId)
  *
  * After these painful steps, you have the contacts of the patient.
  *
  * @param hcparty
  * @param patient (Promise)
  */
	findBy(hcpartyId, patient) {
		return this.crypto.extractDelegationsSFKs(patient, hcpartyId).then(secretForeignKeys => this.api.form().findByHCPartyPatientSecretFKeys(hcpartyId, secretForeignKeys.join(','))).then(forms => this.decrypt(hcpartyId, forms)).then(function (decryptedForms) {
			return decryptedForms;
		});
	}

	decrypt(hcpartyId, forms) {
		return Promise.all(forms.map(form => this.crypto.decryptAndImportAesHcPartyKeysInDelegations(hcpartyId, form.delegations).then(function (decryptedAndImportedAesHcPartyKeys) {
			var collatedAesKeys = {};
			decryptedAndImportedAesHcPartyKeys.forEach(k => collatedAesKeys[k.delegatorId] = k.key);
			return this.crypto.decryptDelegationsSFKs(form.delegations[hcpartyId], collatedAesKeys, form.id).then(sfks => {
				if (form.encryptedContent) {
					return this.crypto.AES.importKey('raw', this.crypto.utils.hex2ua(sfks[0].replace(/-/g, ''))).then(key => new Promise((resolve, reject) => this.crypto.AES.decrypt(key, this.crypto.utils.text2ua(atob(form.encryptedContent))).then(resolve).catch(err => {
						console.log("Error, could not decrypt: " + err);
						resolve(null);
					}))).then(decrypted => {
						if (decrypted) {
							form = _.extend(form, JSON.parse(this.crypto.utils.ua2text(decrypted)));
						}
						return form;
					});
				} else {
					return Promise.resolve(form);
				}
			}).catch(function (e) {
				console.log(e);
			});
		}.bind(this)))).catch(function (e) {
			console.log(e);
		});
	}

}

customElements.define(IccFormXApi.is, IccFormXApi);
</script>

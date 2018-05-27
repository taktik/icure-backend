	<link rel="import" href="../../../bower_components/polymer/polymer.html">

	<dom-module id="icc-patient-x-api">
		<template>
			<style>
			</style>
		</template>
	</dom-module>

	<script>class IccPatientXApi extends Polymer.mixinBehaviors([], Polymer.Element) {
	static get is() {
		return 'icc-patient-x-api';
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
		this.baseApi = this.api.patient();

		const proto = Object.getPrototypeOf(this.baseApi);
		Object.getOwnPropertyNames(proto).forEach(p => {
			if (p !== 'constructor' && p !== 'handleError' && proto[p] && typeof proto[p] === 'function') {
				this[p] = this.baseApi[p].bind(this.baseApi);
			}
		});
	}

	newInstance(user, p) {
		const patient = _.extend({
			id: this.crypto.randomUuid(),
			_type: 'org.taktik.icure.entities.Patient',
			created: new Date().getTime(),
			modified: new Date().getTime(),
			responsible: user.healthcarePartyId,
			author: user.id,
			codes: [],
			tags: []
		}, p || {});

		return this.crypto.initObjectDelegations(patient, null, user.healthcarePartyId, null).then(initData => {
			_.extend(patient, { delegations: initData.delegations });

			let promise = Promise.resolve(patient);
			(user.autoDelegations ? (user.autoDelegations.all || []).concat(user.autoDelegations.medicalInformation || []) : []).forEach(delegateId => promise = promise.then(patient => this.crypto.appendObjectDelegations(patient, null, user.healthcarePartyId, delegateId, initData.secretId)).then(extraData => _.extend(patient, { delegations: extraData.delegations })));
			return promise;
		});
	}

}

customElements.define(IccPatientXApi.is, IccPatientXApi);
</script>

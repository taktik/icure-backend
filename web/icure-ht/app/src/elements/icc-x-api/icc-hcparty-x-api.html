	<link rel="import" href="../../../bower_components/polymer/polymer.html">

	<dom-module id="icc-hcparty-x-api">
		<template>
			<style>
			</style>
		</template>
	</dom-module>

	<script>class IccHcpartyXApi extends Polymer.mixinBehaviors([], Polymer.Element) {
	static get is() {
		return 'icc-hcparty-x-api';
	}

	static get properties() {
		return {
			api: {
				type: Object
			},
			crypto: {
				type: Object
			},
			hcPartyKeysCache: {
				type: Object,
				value: () => ({})
			}
		};
	}

	constructor() {
		super();
	}

	init() {
		this.baseApi = this.api.hcparty();
		const proto = Object.getPrototypeOf(this.baseApi);
		Object.getOwnPropertyNames(proto).forEach(p => {
			if (p !== 'constructor' && p !== 'handleError' && proto[p] && typeof proto[p] === 'function') {
				this[p] = this.baseApi[p].bind(this.baseApi);
			}
		});
	}

	getHcPartyKeysForDelegate(healthcarePartyId) {
		const cached = this.hcPartyKeysCache[healthcarePartyId];
		return cached ? Promise.resolve(cached) : this.api.hcparty().getHcPartyKeysForDelegate(healthcarePartyId).then(r => this.hcPartyKeysCache[healthcarePartyId] = r);
	}

}

customElements.define(IccHcpartyXApi.is, IccHcpartyXApi);
</script>

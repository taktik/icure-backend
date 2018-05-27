	<link rel="import" href="../../../bower_components/polymer/polymer.html">

	<link rel="import" href="../icc-api/icc-api.html">

	<link rel="import" href="icc-contact-x-api.html">
	<link rel="import" href="icc-patient-x-api.html">
	<link rel="import" href="icc-form-x-api.html">
	<link rel="import" href="icc-document-x-api.html">
	<link rel="import" href="icc-code-x-api.html">
	<link rel="import" href="icc-helement-x-api.html">
	<link rel="import" href="icc-hcparty-x-api.html">
	<link rel="import" href="icc-crypto-x-api.html">

	<link rel="import" href="../fhc-api/fhc-api.html">

	<dom-module id="icc-x-api">
		<template>
			<style>
			</style>

			<icc-api id="icc-api" host="[[host]]" headers="[[headers]]" on-refresh="refresh"></icc-api>

			<icc-crypto-x-api id="icc-crypto-x-api" api="[[baseApi]]" hcparty-api="[[hcpartyApi]]"></icc-crypto-x-api>

			<icc-hcparty-x-api id="icc-hcparty-x-api" api="[[baseApi]]" crypto="[[cryptoApi]]"></icc-hcparty-x-api>
			<icc-patient-x-api id="icc-patient-x-api" api="[[baseApi]]" crypto="[[cryptoApi]]"></icc-patient-x-api>
			<icc-contact-x-api id="icc-contact-x-api" api="[[baseApi]]" crypto="[[cryptoApi]]"></icc-contact-x-api>
			<icc-form-x-api id="icc-form-x-api" api="[[baseApi]]" crypto="[[cryptoApi]]"></icc-form-x-api>
			<icc-document-x-api id="icc-document-x-api" api="[[baseApi]]" crypto="[[cryptoApi]]"></icc-document-x-api>
			<icc-code-x-api id="icc-code-x-api" api="[[baseApi]]" crypto="[[cryptoApi]]"></icc-code-x-api>
			<icc-helement-x-api id="icc-helement-x-api" api="[[baseApi]]" crypto="[[cryptoApi]]" contact-api="[[contactApi]]"></icc-helement-x-api>

			<fhc-api id="fhc-api" host="[[fhcHost]]"></fhc-api>
		</template>
	</dom-module>

	<script>
import moment from '../../../bower_components/moment/src/moment';

class IccXApi extends Polymer.mixinBehaviors([], Polymer.Element) {
	static get is() {
		return 'icc-x-api';
	}

	static get properties() {
		return {
			headers: {
				type: Object,
				value: { "Content-Type": "application/json" }
			},
			host: {
				type: String
			},
			fhcHost: {
				type: String
			},
			baseApi: {
				type: Object,
				notify: true
			},
			hcpartyApi: {
				type: Object
			},
			hcParties: {
				type: Object,
				value: function () {
					return {};
				}
			},
			users: {
				type: Object,
				value: function () {
					return {};
				}
			}
		};
	}

	constructor() {
		super();
	}

	ready() {
		super.ready();

		console.log("API ready");

		//Link the apis
		this.set('baseApi', this.$['icc-api']);
		this.set('hcpartyApi', this.$['icc-hcparty-x-api']);
		this.set('cryptoApi', this.$['icc-crypto-x-api']);
		this.set('contactApi', this.$['icc-contact-x-api']);

		Object.getOwnPropertyNames(Object.getPrototypeOf(this.baseApi)).forEach(p => {
			if (typeof this.baseApi[p] === 'function' && !this[p]) {
				this[p] = this.baseApi[p].bind(this.baseApi);
			}
		});

		this.refresh();
	}

	refresh() {
		if (!this.baseApi) {
			return;
		}

		this.patient().init();
		this.contact().init();
		this.form().init();
		this.document().init();
		this.code().init();
		this.helement().init();
		this.hcparty().init();
		this.crypto().init();

		//Load the map of hcps
		this.user().listUsers().then(users => {
			this.set('users', users.rows.reduce((acc, u) => {
				acc[u.id] = u;return acc;
			}, {}));
			return this.hcparty().getHealthcareParties(users.rows.map(u => u.healthcarePartyId).filter(id => id !== null));
		}).then(hcps => this.set('hcParties', hcps.reduce((acc, hcp) => {
			acc[hcp.id] = hcp;return acc;
		}, {})));
	}

	patient() {
		return this.$['icc-patient-x-api'];
	}

	contact() {
		return this.$['icc-contact-x-api'];
	}

	form() {
		return this.$['icc-form-x-api'];
	}

	document() {
		return this.$['icc-document-x-api'];
	}

	code() {
		return this.$['icc-code-x-api'];
	}

	helement() {
		return this.$['icc-helement-x-api'];
	}

	hcparty() {
		return this.$['icc-hcparty-x-api'];
	}

	crypto() {
		return this.$['icc-crypto-x-api'];
	}

	fhc() {
		return this.$['fhc-api'];
	}

	localize(e, lng) {
		if (!e) {
			return null;
		}
		return e[lng] || e.fr || e.en || e.nl;
	}

	//Convenience methods for dates management
	after(d1, d2) {
		return d1 === null || d2 === null || d1 === undefined || d2 === undefined || this.moment(d1).isAfter(this.moment(d2));
	}

	before(d1, d2) {
		return d1 === null || d2 === null || d1 === undefined || d2 === undefined || this.moment(d1).isBefore(this.moment(d2));
	}

	moment(epochOrLongCalendar) {
		if (!epochOrLongCalendar && epochOrLongCalendar !== 0) {
			return null;
		}
		if (epochOrLongCalendar >= 18000101 && epochOrLongCalendar < 25400000) {
			return moment('' + epochOrLongCalendar, 'YYYYMMDD');
		} else if (epochOrLongCalendar >= 18000101000000) {
			return moment('' + epochOrLongCalendar, 'YYYYMMDDhhmmss');
		} else {
			return moment(epochOrLongCalendar);
		}
	}

	template(template, args) {
		const nargs = /\{([0-9a-zA-Z_ ]+)\}/g;
		return template.replace(nargs, function replaceArg(match, i, index) {
			var result;

			if (template[index - 1] === "{" && template[index + match.length] === "}") {
				//Double {{ }} means escape
				return i;
			} else {
				result = args.hasOwnProperty(i) ? args[i] : null;
				if (result === null || result === undefined) {
					return "";
				}

				return result;
			}
		});
	}

	getAuthor(author) {
		const usr = this.users[author];
		const hcp = usr ? this.hcParties[usr.healthcarePartyId] : null;
		return hcp && hcp.lastName + " " + (hcp.firstName && hcp.firstName.length && hcp.firstName.substr(0, 1) + ".") || usr && usr.login || "N/A";
	}

}

customElements.define(IccXApi.is, IccXApi);
</script>

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="fhc-hubcontroller-api">
		<style>
		</style>

		<template>
				<iron-ajax id="getHcpConsentUsingGET" method="GET" headers="[[headers]]" url="/hub/hcpconsent/{hcpNihii}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getPatientConsentUsingGET1" method="GET" headers="[[headers]]" url="/hub/consent/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getPatientUsingGET" method="GET" headers="[[headers]]" url="/hub/patient/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getTherapeuticLinksUsingGET" method="GET" headers="[[headers]]" url="/hub/therlink/{hcpNihii}/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getTransactionSetUsingGET" method="GET" headers="[[headers]]" url="/hub/ts/{ssin}/{sv}/{sl}/{value}" handle-as="document" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getTransactionUsingGET" method="GET" headers="[[headers]]" url="/hub/t/{ssin}/{sv}/{sl}" handle-as="document" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getTransactionsListUsingGET" method="GET" headers="[[headers]]" url="/hub/list/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="putPatientUsingPOST" method="POST" headers="[[headers]]" url="/hub/patient/{lastName}/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="putTransactionSetUsingPOST" method="POST" headers="[[headers]]" url="/hub/ts/{hubId}/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="putTransactionUsingPOST" method="POST" headers="[[headers]]" url="/hub/t/{hubId}/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="registerPatientConsentUsingPOST1" method="POST" headers="[[headers]]" url="/hub/consent/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="registerTherapeuticLinkUsingPOST" method="POST" headers="[[headers]]" url="/hub/therlink/{hcpNihii}/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
		</template>
	</dom-module>`;

document.head.appendChild($_documentContainer.content);
Polymer({
    is: 'fhc-hubcontroller-api',
    properties: {
        headers: {
						type: Object,
						value: {"Content-Type": "application/json"}
        },
        host: {
						type: String
        }
    },
    behaviors: [],
    getHcpConsentUsingGET: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip) {
        var xhr = this.$.getHcpConsentUsingGET
        
        xhr.url = this.host+"/hub/hcpconsent/{hcpNihii}".replace("{hcpNihii}", hcpNihii) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    getPatientConsentUsingGET1: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip, patientSsin) {
        var xhr = this.$.getPatientConsentUsingGET1
        
        xhr.url = this.host+"/hub/consent/{patientSsin}".replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    getPatientUsingGET: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip, patientSsin) {
        var xhr = this.$.getPatientUsingGET
        
        xhr.url = this.host+"/hub/patient/{patientSsin}".replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    getTherapeuticLinksUsingGET: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip, patientSsin, therLinkType, from, to) {
        var xhr = this.$.getTherapeuticLinksUsingGET
        
        xhr.url = this.host+"/hub/therlink/{hcpNihii}/{patientSsin}".replace("{hcpNihii}", hcpNihii).replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (therLinkType ? "&therLinkType=" + therLinkType : "") + (from ? "&from=" + from : "") + (to ? "&to=" + to : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    getTransactionSetUsingGET: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip, ssin, sv, sl, value) {
        var xhr = this.$.getTransactionSetUsingGET
        
        xhr.url = this.host+"/hub/ts/{ssin}/{sv}/{sl}".replace("{ssin}", ssin).replace("{sv}", sv).replace("{sl}", sl) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (value ? "&id=" + value : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    getTransactionUsingGET: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip, ssin, sv, sl, value) {
        var xhr = this.$.getTransactionUsingGET
        
        xhr.url = this.host+"/hub/t/{ssin}/{sv}/{sl}".replace("{ssin}", ssin).replace("{sv}", sv).replace("{sl}", sl) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (value ? "&id=" + value : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    getTransactionsListUsingGET: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip, patientSsin, from, to, authorNihii, authorSsin, isGlobal) {
        var xhr = this.$.getTransactionsListUsingGET

        xhr.url = this.host+"/hub/list/{patientSsin}".replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (from ? "&from=" + from : "") + (to ? "&to=" + to : "") + (authorNihii ? "&authorNihii=" + authorNihii : "") + (authorSsin ? "&authorSsin=" + authorSsin : "") + (isGlobal ? "&isGlobal=" + isGlobal : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    putPatientUsingPOST: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpZip, patientSsin, firstName, lastName, gender, dateOfBirth) {
        var xhr = this.$.putPatientUsingPOST
        
        xhr.url = this.host+"/hub/patient/{lastName}/{patientSsin}".replace("{patientSsin}", patientSsin).replace("{lastName}", lastName) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (firstName ? "&firstName=" + firstName : "") + (gender ? "&gender=" + gender : "") + (dateOfBirth ? "&dateOfBirth=" + dateOfBirth : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    putTransactionSetUsingPOST: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpZip, hubId, patientSsin, message, hubApplication) {
        var xhr = this.$.putTransactionSetUsingPOST
        xhr.body = message
        xhr.url = this.host+"/hub/ts/{hubId}/{patientSsin}".replace("{hubId}", hubId).replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (hubApplication ? "&hubApplication=" + hubApplication : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    putTransactionUsingPOST: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpZip, hubId, patientSsin, message, hubApplication) {
        var xhr = this.$.putTransactionUsingPOST
        xhr.body = message
        xhr.url = this.host+"/hub/t/{hubId}/{patientSsin}".replace("{hubId}", hubId).replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (hubApplication ? "&hubApplication=" + hubApplication : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    registerPatientConsentUsingPOST1: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpZip, patientSsin, patientEidCardNumber) {
        var xhr = this.$.registerPatientConsentUsingPOST1
        
        xhr.url = this.host+"/hub/consent/{patientSsin}".replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (patientEidCardNumber ? "&patientEidCardNumber=" + patientEidCardNumber : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    registerTherapeuticLinkUsingPOST: function (endpoint, keystoreId, tokenId, passPhrase, hcpNihii, hcpLastName, hcpFirstName, hcpSsin, hcpZip, patientSsin, patientEidCardNumber) {
        var xhr = this.$.registerTherapeuticLinkUsingPOST
        
        xhr.url = this.host+"/hub/therlink/{hcpNihii}/{patientSsin}".replace("{hcpNihii}", hcpNihii).replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime()  + (endpoint ? "&endpoint=" + endpoint : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpZip ? "&hcpZip=" + hcpZip : "") + (patientEidCardNumber ? "&patientEidCardNumber=" + patientEidCardNumber : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    handleError: function (e) {
        if (e.detail.request.status === 401) this.dispatchEvent(new CustomEvent('auth-failed', { bubbles: true, composed: true })); else this.dispatchEvent(new CustomEvent('api-error', { detail: e, bubbles: true, composed: true }))
    }
});

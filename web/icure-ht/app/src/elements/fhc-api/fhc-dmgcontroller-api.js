const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="fhc-dmgcontroller-api">
		<style>
		</style>

		<template>
				<iron-ajax id="confirmAcksUsingPOST" method="POST" headers="[[headers]]" url="/gmd/confirm/acks" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="confirmDmgMessagesUsingPOST" method="POST" headers="[[headers]]" url="/gmd/confirm/messages" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="consultDmgUsingGET" method="GET" headers="[[headers]]" url="/gmd" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getDmgMessagesUsingGET" method="GET" headers="[[headers]]" url="/gmd/messages" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="notifyDmgUsingPOST" method="POST" headers="[[headers]]" url="/gmd/notify/{nomenclature}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="postDmgsListRequestUsingPOST" method="POST" headers="[[headers]]" url="/gmd/reqlist" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="registerDoctorUsingPOST" method="POST" headers="[[headers]]" url="/gmd/register/{oa}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
		</template>
	</dom-module>`;

document.head.appendChild($_documentContainer.content);
Polymer({
    is: 'fhc-dmgcontroller-api',
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
    confirmAcksUsingPOST: function (keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, dmgTacks) {
        var xhr = this.$.confirmAcksUsingPOST
        xhr.body = dmgTacks
        xhr.url = this.host+"/gmd/confirm/acks" + "?ts=" + new Date().getTime()  + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    confirmDmgMessagesUsingPOST: function (keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, dmgMessages) {
        var xhr = this.$.confirmDmgMessagesUsingPOST
        xhr.body = dmgMessages
        xhr.url = this.host+"/gmd/confirm/messages" + "?ts=" + new Date().getTime()  + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    consultDmgUsingGET: function (keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, patientSsin, patientGender, oa, regNrWithMut, requestDate) {
        var xhr = this.$.consultDmgUsingGET
        
        xhr.url = this.host+"/gmd" + "?ts=" + new Date().getTime()  + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (patientSsin ? "&patientSsin=" + patientSsin : "") + (patientGender ? "&patientGender=" + patientGender : "") + (oa ? "&oa=" + oa : "") + (regNrWithMut ? "&regNrWithMut=" + regNrWithMut : "") + (requestDate ? "&requestDate=" + requestDate : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    getDmgMessagesUsingGET: function (keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, oa, messageNames) {
        var xhr = this.$.getDmgMessagesUsingGET
        xhr.body = messageNames
        xhr.url = this.host+"/gmd/messages" + "?ts=" + new Date().getTime()  + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (oa ? "&oa=" + oa : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    notifyDmgUsingPOST: function (keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, nomenclature, patientSsin, oa, regNrWithMut, patientFirstName, patientLastName, patientGender, requestDate) {
        var xhr = this.$.notifyDmgUsingPOST
        
        xhr.url = this.host+"/gmd/notify/{nomenclature}".replace("{nomenclature}", nomenclature) + "?ts=" + new Date().getTime()  + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (patientSsin ? "&patientSsin=" + patientSsin : "") + (oa ? "&oa=" + oa : "") + (regNrWithMut ? "&regNrWithMut=" + regNrWithMut : "") + (patientFirstName ? "&patientFirstName=" + patientFirstName : "") + (patientLastName ? "&patientLastName=" + patientLastName : "") + (patientGender ? "&patientGender=" + patientGender : "") + (requestDate ? "&requestDate=" + requestDate : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    postDmgsListRequestUsingPOST: function (keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, oa, requestDate) {
        var xhr = this.$.postDmgsListRequestUsingPOST
        
        xhr.url = this.host+"/gmd/reqlist" + "?ts=" + new Date().getTime()  + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (oa ? "&oa=" + oa : "") + (requestDate ? "&requestDate=" + requestDate : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    registerDoctorUsingPOST: function (keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, oa, bic, iban) {
        var xhr = this.$.registerDoctorUsingPOST
        
        xhr.url = this.host+"/gmd/register/{oa}".replace("{oa}", oa) + "?ts=" + new Date().getTime()  + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (bic ? "&bic=" + bic : "") + (iban ? "&iban=" + iban : "")
        return xhr.generateRequest().completes.then(function (req) {
						return req.response
        })
    },
    handleError: function (e) {
        if (e.detail.request.status === 401) this.dispatchEvent(new CustomEvent('auth-failed', { bubbles: true, composed: true })); else this.dispatchEvent(new CustomEvent('api-error', { detail: e, bubbles: true, composed: true }))
    },

});

import {PolymerElement, html} from '@polymer/polymer';
class FhcStscontrollerApi extends PolymerElement {
  static get template() {
    return html`
    		<style>
	    	</style>

				<iron-ajax id="registerTokenUsingPOST" method="POST" headers="[[headers]]" url="/sts/token/{tokenId}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="requestTokenUsingGET" method="GET" headers="[[headers]]" url="/sts/token/{keystoreId}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="uploadKeystoreUsingPOST" method="POST" headers="[[headers]]" url="/sts/keystore" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
`;
  }

  static get is() {
      return "fhc-stscontroller-api";
	}

  static get properties() {
      return {
          headers: {
              type: Object,
              value: { "Content-Type": "application/json" }
          },
          host: {
              type: String
          }
      };
	}

  constructor() {
      super();
	}

  registerTokenUsingPOST(token, tokenId) {
      var xhr = this.$.registerTokenUsingPOST;
      xhr.body = token;

      xhr.url = this.host + "/sts/token/{tokenId}".replace("{tokenId}", tokenId) + "?ts=" + new Date().getTime();
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  requestTokenUsingGET(passPhrase, ssin, keystoreId) {
      var xhr = this.$.requestTokenUsingGET;

      xhr.url = this.host + "/sts/token/{keystoreId}".replace("{keystoreId}", keystoreId) + "?ts=" + new Date().getTime() + (passPhrase ? "&passPhrase=" + passPhrase : "") + (ssin ? "&ssin=" + ssin : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  checkTokenUsingGET(tokenId) {
      var xhr = this.$.requestTokenUsingGET;

      xhr.url = this.host + "/sts/token/check/{tokenId}".replace("{tokenId}", tokenId) + "?ts=" + new Date().getTime();
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  checkKeystoreUsingGET(keystoreId) {
      var xhr = this.$.requestTokenUsingGET;

      xhr.url = this.host + "/sts/keystore/check/{keystoreId}".replace("{keystoreId}", keystoreId) + "?ts=" + new Date().getTime();
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  uploadKeystoreUsingPOST(file) {
      var xhr = this.$.uploadKeystoreUsingPOST;(xhr.body = xhr.body || new FormData()).append('file', new Blob(file, { type: "application/octet-stream" }));
      xhr.set('headers', {});
      xhr.url = this.host + "/sts/keystore" + "?ts=" + new Date().getTime();
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  handleError(e) {
      if (e.detail.request.status === 401) this.dispatchEvent(new CustomEvent('auth-failed', { bubbles: true, composed: true })); else this.dispatchEvent(new CustomEvent('api-error', { detail: e, bubbles: true, composed: true }))
	}

  mpfdHeaders(hdrs) {
      return _.extend(_.extend({}, hdrs), { 'content-type': 'multipart/form-data' });
	}
}

customElements.define(FhcStscontrollerApi.is, FhcStscontrollerApi);

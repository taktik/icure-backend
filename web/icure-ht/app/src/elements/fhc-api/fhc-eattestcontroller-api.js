import {PolymerElement, html} from '@polymer/polymer';
class FhcEattestcontrollerApi extends PolymerElement {
  static get template() {
    return html`
    		<style>
	    	</style>

				<iron-ajax id="sendAttestUsingPOST" method="POST" headers="[[headers]]" url="/eattest/send/{patientSsin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="sendAttestWithResponseUsingPOST" method="POST" headers="[[headers]]" url="/eattest/send/{patientSsin}/verbose" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
`;
  }

  static get is() {
      return "fhc-eattestcontroller-api";
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

  sendAttestUsingPOST(patientSsin, keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, hcpCbe, attest, date) {
      var xhr = this.$.sendAttestUsingPOST;
      xhr.body = attest;

      xhr.url = this.host + "/eattest/send/{patientSsin}".replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (hcpCbe ? "&hcpCbe=" + hcpCbe : "") + (date ? "&date=" + date : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  sendAttestWithResponseUsingPOST(patientSsin, keystoreId, tokenId, passPhrase, hcpNihii, hcpSsin, hcpFirstName, hcpLastName, hcpCbe, attest, date) {
      var xhr = this.$.sendAttestWithResponseUsingPOST;
      xhr.body = attest;

      xhr.url = this.host + "/eattest/send/{patientSsin}/verbose".replace("{patientSsin}", patientSsin) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpFirstName ? "&hcpFirstName=" + hcpFirstName : "") + (hcpLastName ? "&hcpLastName=" + hcpLastName : "") + (hcpCbe ? "&hcpCbe=" + hcpCbe : "") + (date ? "&date=" + date : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  handleError(e) {
      if (e.detail.request.status === 401) this.dispatchEvent(new CustomEvent('auth-failed', { bubbles: true, composed: true })); else this.dispatchEvent(new CustomEvent('api-error', { detail: e, bubbles: true, composed: true }))
	}
}

customElements.define(FhcEattestcontrollerApi.is, FhcEattestcontrollerApi);

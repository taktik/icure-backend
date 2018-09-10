import {PolymerElement, html} from '@polymer/polymer';
class FhcGeninscontrollerApi extends PolymerElement {
  static get template() {
    return html`
    		<style>
	    	</style>

				<iron-ajax id="getGeneralInsurabilityByMembershipUsingGET" method="GET" headers="[[headers]]" url="/genins/{io}/{ioMembership}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getGeneralInsurabilityUsingGET" method="GET" headers="[[headers]]" url="/genins/{ssin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
`;
  }

  static get is() {
      return "fhc-geninscontroller-api";
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

  getGeneralInsurabilityByMembershipUsingGET(io, ioMembership, tokenId, keystoreId, passPhrase, hcpNihii, hcpSsin, hcpName, hcpQuality, date, hospitalized) {
      var xhr = this.$.getGeneralInsurabilityByMembershipUsingGET;

      xhr.url = this.host + "/genins/{io}/{ioMembership}".replace("{io}", io).replace("{ioMembership}", ioMembership) + "?ts=" + new Date().getTime() + (tokenId ? "&tokenId=" + tokenId : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpName ? "&hcpName=" + hcpName : "") + (hcpQuality ? "&hcpQuality=" + hcpQuality : "") + (date ? "&date=" + date : "") + (hospitalized ? "&hospitalized=" + hospitalized : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  getGeneralInsurabilityUsingGET(ssin, tokenId, keystoreId, passPhrase, hcpNihii, hcpSsin, hcpName, hcpQuality, date, hospitalized) {
      var xhr = this.$.getGeneralInsurabilityUsingGET;

      xhr.url = this.host + "/genins/{ssin}".replace("{ssin}", ssin) + "?ts=" + new Date().getTime() + (tokenId ? "&tokenId=" + tokenId : "") + (keystoreId ? "&keystoreId=" + keystoreId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (hcpNihii ? "&hcpNihii=" + hcpNihii : "") + (hcpSsin ? "&hcpSsin=" + hcpSsin : "") + (hcpName ? "&hcpName=" + hcpName : "") + (hcpQuality ? "&hcpQuality=" + hcpQuality : "") + (date ? "&date=" + date : "") + (hospitalized ? "&hospitalized=" + hospitalized : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  handleError(e) {
      if (e.detail.request.status === 401) this.dispatchEvent(new CustomEvent('auth-failed', { bubbles: true, composed: true })); else this.dispatchEvent(new CustomEvent('api-error', { detail: e, bubbles: true, composed: true }))
	}
}

customElements.define(FhcGeninscontrollerApi.is, FhcGeninscontrollerApi);

class FhcAddressbookcontrollerApi extends Polymer.Element {
  static get template() {
    return Polymer.html`
    		<style>
	    	</style>

				<iron-ajax id="getHcpByNihiiUsingGET" method="GET" headers="[[headers]]" url="/ab/hcp/nihii/{nihii}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getHcpBySsinUsingGET" method="GET" headers="[[headers]]" url="/ab/hcp/ssin/{ssin}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getOrgByCbeUsingGET" method="GET" headers="[[headers]]" url="/ab/org/cbe/{cbe}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getOrgByEhpUsingGET" method="GET" headers="[[headers]]" url="/ab/org/ehp/{ehp}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="getOrgByNihiiUsingGET" method="GET" headers="[[headers]]" url="/ab/org/nihii/{nihii}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="searchHcpUsingGET" method="GET" headers="[[headers]]" url="/ab/search/hcp/{lastName}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
				<iron-ajax id="searchOrgUsingGET" method="GET" headers="[[headers]]" url="/ab/search/org/{name}" handle-as="json" on-error="handleError" with-credentials=""></iron-ajax>
`;
  }

  static get is() {
      return "fhc-addressbookcontroller-api";
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

  getHcpByNihiiUsingGET(keystoreId, tokenId, passPhrase, nihii, language) {
      var xhr = this.$.getHcpByNihiiUsingGET;

      xhr.url = this.host + "/ab/hcp/nihii/{nihii}".replace("{nihii}", nihii) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (language ? "&language=" + language : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  getHcpBySsinUsingGET(keystoreId, tokenId, passPhrase, ssin, language) {
      var xhr = this.$.getHcpBySsinUsingGET;

      xhr.url = this.host + "/ab/hcp/ssin/{ssin}".replace("{ssin}", ssin) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (language ? "&language=" + language : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  getOrgByCbeUsingGET(keystoreId, tokenId, passPhrase, cbe, language) {
      var xhr = this.$.getOrgByCbeUsingGET;

      xhr.url = this.host + "/ab/org/cbe/{cbe}".replace("{cbe}", cbe) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (language ? "&language=" + language : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  getOrgByEhpUsingGET(keystoreId, tokenId, passPhrase, ehp, language) {
      var xhr = this.$.getOrgByEhpUsingGET;

      xhr.url = this.host + "/ab/org/ehp/{ehp}".replace("{ehp}", ehp) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (language ? "&language=" + language : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  getOrgByNihiiUsingGET(keystoreId, tokenId, passPhrase, nihii, language) {
      var xhr = this.$.getOrgByNihiiUsingGET;

      xhr.url = this.host + "/ab/org/nihii/{nihii}".replace("{nihii}", nihii) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (language ? "&language=" + language : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  searchHcpUsingGET(keystoreId, tokenId, passPhrase, lastName, firstName, type) {
      var xhr = this.$.searchHcpUsingGET;

      xhr.url = this.host + "/ab/search/hcp/{lastName}".replace("{lastName}", lastName) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (firstName ? "&firstName=" + firstName : "") + (type ? "&type=" + type : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  searchOrgUsingGET(keystoreId, tokenId, passPhrase, name, type) {
      var xhr = this.$.searchOrgUsingGET;

      xhr.url = this.host + "/ab/search/org/{name}".replace("{name}", name) + "?ts=" + new Date().getTime() + (keystoreId ? "&keystoreId=" + keystoreId : "") + (tokenId ? "&tokenId=" + tokenId : "") + (passPhrase ? "&passPhrase=" + passPhrase : "") + (type ? "&type=" + type : "");
      return xhr.generateRequest().completes.then(function (req) {
          return req.response;
      });
	}

  handleError(e) {
      if (e.detail.request.status === 401) this.dispatchEvent(new CustomEvent('auth-failed', { bubbles: true, composed: true })); else this.dispatchEvent(new CustomEvent('api-error', { detail: e, bubbles: true, composed: true }))
	}
}

customElements.define(FhcAddressbookcontrollerApi.is, FhcAddressbookcontrollerApi);

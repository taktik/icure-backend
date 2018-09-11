import * as api from './fhcApi'
import {PolymerElement, html} from '@polymer/polymer';

class FhcApi extends PolymerElement {
    static get template() {
        return html``;
    }

    static get is() {
        return 'fhc-api'
    }

    static get properties() {
        return {
            headers: {
                type: Object,
                value: {"Content-Type": "application/json"},
                notify: true
            },
            host: {
                type: String
            },
        }
    }

    static get observers() {
        return ["refresh(headers, headers.*, host)"]
    }

    constructor() {
        super()
    }

    ready() {
        super.ready()
        if (this.host != null && this.headers != null) this.refresh()
    }

    refresh() {
        this.fhcAddressbookcontrollerApi = new api.fhcAddressbookcontrollerApi(this.host, this.headers)
        this.fhcBasicerrorcontrollerApi = new api.fhcBasicerrorcontrollerApi(this.host, this.headers)
        this.fhcConsentcontrollerApi = new api.fhcConsentcontrollerApi(this.host, this.headers)
        this.fhcDmgcontrollerApi = new api.fhcDmgcontrollerApi(this.host, this.headers)
        this.fhcEattestcontrollerApi = new api.fhcEattestcontrollerApi(this.host, this.headers)
        this.fhcEhboxcontrollerApi = new api.fhcEhboxcontrollerApi(this.host, this.headers)
        this.fhcGeninscontrollerApi = new api.fhcGeninscontrollerApi(this.host, this.headers)
        this.fhcHubcontrollerApi = new api.fhcHubcontrollerApi(this.host, this.headers)
        this.fhcRecipecontrollerApi = new api.fhcRecipecontrollerApi(this.host, this.headers)
        this.fhcStscontrollerApi = new api.fhcStscontrollerApi(this.host, this.headers)

        this.dispatchEvent(new CustomEvent('refresh', {detail: {}}))
    }

    fhcAddressbookcontrollerApi() {
        return this.fhcAddressbookcontrollerApi
    }

    fhcBasicerrorcontrollerApi() {
        return this.fhcBasicerrorcontrollerApi
    }

    fhcConsentcontrollerApi() {
        return this.fhcConsentcontrollerApi
    }

    fhcDmgcontrollerApi() {
        return this.fhcDmgcontrollerApi
    }

    fhcEattestcontrollerApi() {
        return this.fhcEattestcontrollerApi
    }

    fhcEhboxcontrollerApi() {
        return this.fhcEhboxcontrollerApi
    }

    fhcGeninscontrollerApi() {
        return this.fhcGeninscontrollerApi
    }

    fhcHubcontrollerApi() {
        return this.fhcHubcontrollerApi
    }

    fhcRecipecontrollerApi() {
        return this.fhcRecipecontrollerApi
    }

    fhcStscontrollerApi() {
        return this.fhcStscontrollerApi
    }
}

customElements.define(FhcApi.is, FhcApi)



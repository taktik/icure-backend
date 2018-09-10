import './fhc-addressbookcontroller-api.js';
import './fhc-basicerrorcontroller-api.js';
import './fhc-consentcontroller-api.js';
import './fhc-eattestcontroller-api.js';
import './fhc-ehboxcontroller-api.js';
import './fhc-geninscontroller-api.js';
import './fhc-hubcontroller-api.js';
import './fhc-recipecontroller-api.js';
import './fhc-stscontroller-api.js';
import './fhc-therlinkcontroller-api.js';
import './fhc-dmgcontroller-api.js';
import {PolymerElement, html} from '@polymer/polymer';
class FhcApi extends PolymerElement {
  static get template() {
    return html`
        <style>
        </style>

        <fhc-addressbookcontroller-api id="fhc-addressbookcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-addressbookcontroller-api>
        <fhc-basicerrorcontroller-api id="fhc-basicerrorcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-basicerrorcontroller-api>
        <fhc-consentcontroller-api id="fhc-consentcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-consentcontroller-api>
        <fhc-eattestcontroller-api id="fhc-eattestcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-eattestcontroller-api>
        <fhc-ehboxcontroller-api id="fhc-ehboxcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-ehboxcontroller-api>
        <fhc-geninscontroller-api id="fhc-geninscontroller-api" host="[[host]]" headers="[[headers]]"></fhc-geninscontroller-api>
        <fhc-hubcontroller-api id="fhc-hubcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-hubcontroller-api>
        <fhc-recipecontroller-api id="fhc-recipecontroller-api" host="[[host]]" headers="[[headers]]"></fhc-recipecontroller-api>
        <fhc-stscontroller-api id="fhc-stscontroller-api" host="[[host]]" headers="[[headers]]"></fhc-stscontroller-api>
        <fhc-therlinkcontroller-api id="fhc-therlinkcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-therlinkcontroller-api>
        <fhc-dmgcontroller-api id="fhc-dmgcontroller-api" host="[[host]]" headers="[[headers]]"></fhc-dmgcontroller-api>
`;
  }

  static get is() {
      return "fhc-api";
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

  Addressbookcontroller() {
      return this.$['fhc-addressbookcontroller-api'];
  }

  Basicerrorcontroller() {
      return this.$['fhc-basicerrorcontroller-api'];
  }

  Consentcontroller() {
      return this.$['fhc-consentcontroller-api'];
  }

  Eattestcontroller() {
      return this.$['fhc-eattestcontroller-api'];
  }

  Ehboxcontroller() {
      return this.$['fhc-ehboxcontroller-api'];
  }

  Geninscontroller() {
      return this.$['fhc-geninscontroller-api'];
  }

  Hubcontroller() {
      return this.$['fhc-hubcontroller-api'];
  }

  Recipecontroller() {
      return this.$['fhc-recipecontroller-api'];
  }

  Stscontroller() {
      return this.$['fhc-stscontroller-api'];
  }

  Therlinkcontroller() {
      return this.$['fhc-therlinkcontroller-api'];
  }

  DMGcontroller() {
      return this.$['fhc-dmgcontroller-api'];
  }
}

customElements.define(FhcApi.is, FhcApi);

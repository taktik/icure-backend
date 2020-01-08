import '../qrcode-manager/qrcode-capture.js';
import {PolymerElement, html} from '@polymer/polymer';
class HtAppRegisterKeypairDialog extends TkLocalizerMixin(PolymerElement) {
  static get template() {
    return html`
		<style>
		</style>

		<paper-dialog id="registerKeyPairDialog" opened="{{opened}}" modal="">
			<form is="form" id="keys-form">
				<div class="layout vertical center">
					<h3>[[localize('a_pri_key_fil_is_nee_for_thi_use','A private key file is needed for this user',language)]]</h3>
					<h5>ID: {{user.healthcarePartyId}}</h5>
					<paper-input type="file" id="rsaFileInput" on-change="rsaKeySelected"></paper-input>
					<paper-button raised="true" id="submitButton" type="submit" on-tap="selectFile" autofocus="">[[localize('imp','Import',language)]]</paper-button>
					<div>{{message}}</div>
				</div>
			</form>

			<div class="layout vertical center">
				<span>[[captureMessage]]</span>
				<qrcode-capture i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" id="capture" width="640px" height="360px" on-qrcode-detected="_qrCodeDetected"></qrcode-capture>
			</div>

		</paper-dialog>
`;
  }

  static get is() {
      return 'ht-app-register-keypair-dialog';
	}

  static get properties() {
      return {
          user: {
              type: Object,
              value: {}
          },
          api: {
              type: Object,
              value: {}
          },
          message: {
              type: String,
              value: ""
          },
          opened: {
              type: Boolean,
              value: false,
              notify: true,
              observer: '_openedChanged'
          }
      };
	}

  constructor() {
      super();
	}

  _openedChanged() {
      if (this.opened) {
          this.codeParts = [];
          this.$.capture.start();
      } else {
          this.$.capture.stop();
      }
	}

  rsaKeySelected() {
      this.selectedRsaFile = this.$.rsaFileInput.inputElement && this.$.rsaFileInput.inputElement.inputElement ? this.$.rsaFileInput.inputElement.inputElement.files[0] : null;
	}

  selectFile() {
      console.log('File selected');
      this.dispatchEvent(new CustomEvent('file-selected', { detail: this.selectedRsaFile, bubbles: true, composed: true }))
	}

  _qrCodeDetected(e, code) {
      if (!code) {
          return;
      }
      let combinedCode = null;
      if (code.startsWith('{')) {
          combinedCode = code;
      }
      let match = code.match(/^([1-9])([1-9])/);
      if (match) {
          this.codeParts[parseInt(match[1]) - 1] = code.substr(2);
          const codePartsNumber = parseInt(match[2]);
          const range = Array.from(new Array(codePartsNumber).keys());
          combinedCode = range.reduce((acc, val) => this.codeParts[val] && acc !== null ? acc + this.codeParts[val] : null, '');
          const status = range.reduce((acc, val) => {
              if (this.codeParts[val]) {
                  acc[0] += 1;
                  acc[1][val] = 'X';
              } else {
                  acc[1][val] = '.';
              }
              return acc;
          }, [0, []]);
          this.set('captureMessage', `${status[0]}/${codePartsNumber} codes captured (${status[1].join(' ')})`);
      }
      const jwk = combinedCode && (combinedCode.startsWith('{') && JSON.parse(combinedCode) || { alg: 'RSA-OAEP', key_ops: ['decrypt'], kty: 'RSA', ext: true });
      if (jwk && jwk.alg) {
          if (Object.keys(jwk).length === 4) {
              [jwk.d, jwk.dp, jwk.dq, jwk.p, jwk.q, jwk.qi] = String.fromCharCode.apply(null, pako.inflate(this.api.crypto().utils.hex2ua(combinedCode))).split(' ');
          }
          this.set('rsaKey', jwk);
          this.dispatchEvent(new CustomEvent('key-scanned', { detail: jwk, bubbles: true, composed: true }))
      }
	}

  reset() {
      this.selectedRsaFile = null;
      this.$.rsaFileInput.inputElement && this.$.rsaFileInput.inputElement.inputElement && (this.$.rsaFileInput.inputElement.inputElement.value = "");
	}
}

customElements.define(HtAppRegisterKeypairDialog.is, HtAppRegisterKeypairDialog);

import '../qrcode-manager/qrcode-printer.js';
class HtImportKeychain extends Polymer.TkLocalizerMixin(Polymer.mixinBehaviors([Polymer.IronResizableBehavior], Polymer.Element)) {
  static get template() {
    return Polymer.html`
		<style>
			paper-dialog {
				width: 60%;
				min-height: 300px;
			}

			paper-input {
				--paper-input-container-focus-color: var(--app-primary-color);
				--paper-input-container-label: {
					color:var(--app-text-color);
					opacity:1;
				};
			}

			.modal-button{
				--paper-button-ink-color: var(--app-secondary-color-dark);
				color: var(--app-text-color);
				font-weight: 700;
				font-size: 14px;
				height: 40px;
				min-width: 100px;
				padding: 10px 1.2em;
			}

			.modal-button--save{
				box-shadow: var(--shadow-elevation-2dp_-_box-shadow);
				background: var(--app-secondary-color);
			}

		</style>

		<paper-dialog id="dialog" opened="{{opened}}">
			<form is="form" id="keys-form">
				<div class="layout vertical center">
					<h3>[[localize('ple_sel_the_ehe_key','Please select the eHealth keychain',language)]]</h3>
					<h5>[[localize('it_is_usu_loc_in_the_ehe_dir','it is usually located in the eHeath directory',language)]]</h5>
					<paper-input type="file" id="ehKeychainFileInput" on-change="ehKeychainSelected"></paper-input>
					<div>{{message}}</div>
				</div>
			</form>

				<div class="buttons">
					<paper-button dialog-dismiss="" class="modal-button modal-button--cancel">[[localize('can','Cancel',language)]]</paper-button>
					<paper-button dialog-confirm="" autofocus="" on-tap="confirm" class="modal-button modal-button--save">[[localize('sel','Select',language)]]</paper-button>
				</div>
		</paper-dialog>
`;
  }

  static get is() {
      return 'ht-import-keychain';
	}

  static get properties() {
      return {
          api: {
              type: Object,
              value: null
          },
          user: {
              type: Object,
              value: null
          },
          opened: {
              type: Boolean,
              value: false
          }
      };
	}

  static get observers() {
      return ['apiReady(api,user,opened)'];
	}

  ready() {
      super.ready();
      this.addEventListener('iron-resize', () => this.onWidthChange());
	}

  apiReady() {
      if (!this.api || !this.user || !this.user.id || !this.opened) return;
	}

  ehKeychainSelected() {
      this.selectedEhKeychain = this.$.ehKeychainFileInput.inputElement && this.$.ehKeychainFileInput.inputElement.inputElement ? this.$.ehKeychainFileInput.inputElement.inputElement.files[0] : null;
	}

  confirm() {
      const fr = new FileReader();
      return new Promise((resolve, reject) => {
          fr.onerror = reject;
          fr.onabort = reject;
          fr.onload = function (e) {
              const keychain = e.target.result;
              this.api.crypto().saveKeychainInBrowserLocalStorage(this.user.healthcarePartyId, keychain);
          }.bind(this);
          fr.readAsArrayBuffer(this.selectedEhKeychain);
      });

      this.dispatchEvent(new CustomEvent('file-selected', { detail: this.selectedEhKeychain, bubbles: true, composed: true }))
	}

  attached() {
      super.attached();
      this.async(this.notifyResize, 1);
	}

  onWidthChange() {
      const offsetWidth = this.$.dialog.offsetWidth;
      const offsetHeight = this.$.dialog.offsetHeight;
      if (!offsetWidth || !offsetHeight) {
          return;
      }
	}

  open() {
      this.$.dialog.open();
	}

  close() {
      this.$.dialog.close();
	}
}
customElements.define(HtImportKeychain.is, HtImportKeychain);

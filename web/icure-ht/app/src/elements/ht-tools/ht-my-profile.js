import '../qrcode-manager/qrcode-printer.js';
class HtExportKey extends Polymer.TkLocalizerMixin(Polymer.mixinBehaviors([Polymer.IronResizableBehavior], Polymer.Element)) {
  static get template() {
    return Polymer.html`
		<style>
			paper-dialog {
				width: 60%;
				display:flex;
				flex-flow: row wrap;
				justify-content: space-between;
				align-items: flex-start;
			}
			paper-input{
				--paper-input-container-focus-color: var(--app-primary-color);
				font-size:var(--form-font-size);
			}

			.error {
				color: #e53935;
			}

			.buttons{
				width:100%;
			}

			.modal-button{
				--paper-button-ink-color: var(--app-secondary-color-dark);
				color: var(--app-text-color);
				font-weight: 400;
				font-size: 14px;
				height: 40px;
				min-width: 100px;
				padding: 10px 1.2em;
			}

			.modal-button--save{
				box-shadow: var(--shadow-elevation-2dp_-_box-shadow);
				background: var(--app-secondary-color);
				color: var(--app-primary-color-dark);
				font-weight: 700;

			}
			.left-col, .right-col{
				flex-grow: 1;
				width: 50%;
				box-sizing: border-box;
			}
			#dialog{
				overflow: auto;
			}
			#printable {
				width: fit-content;
			}

		</style>

		<paper-dialog id="dialog" opened="{{opened}}">
			<div class="left-col">
				<vaadin-form-layout>
					<paper-input colspan="2" label="Full name" value="{{user.name}}"></paper-input>
					<paper-input colspan="2" label="Group ID" value="{{user.groupId}}"></paper-input>
					<paper-input label="Login" value="{{user.login}}"></paper-input>
					<paper-input label="Email" value="{{user.email}}"></paper-input>
					<paper-input label="Auto logout delay (minute)" type="number" min="0" value=""></paper-input>
				</vaadin-form-layout>
			</div>
			<div class="right-col">
				<vaadin-form-layout>
					<paper-radio-group selected="{{hcp.languages.0}}">
						<paper-radio-button name="en">[[localize('eng','English',language)]]</paper-radio-button>
						<paper-radio-button name="fr">[[localize('fre','French',language)]]</paper-radio-button>
						<paper-radio-button name="nl">[[localize('dut','Dutch',language)]]</paper-radio-button>
					</paper-radio-group>
					<paper-input label="Password" value="{{userPassword}}" type="password"></paper-input>
					<paper-input label="Confirmation" value="{{userConfirmation}}" type="password"></paper-input>
					<template is="dom-if" if="[[_mismatch(userPassword,userConfirmation)]]">
						<div class="error">Passwords do not match</div>
					</template>
					<template is="dom-if" if="[[_tooShort(userPassword)]]">
						<div class="error">Passwords must be at least 8 characters</div>
					</template>
					<vaadin-checkbox colspan="2" checked="{{user.use2fa}}">[[localize('use_two_fac_aut','Use two factors Authentication',language)]]</vaadin-checkbox>
				</vaadin-form-layout>
				<div id="printable">
					<qrcode-printer i18n="[[i18n]]" language="[[language]]" resources="[[resources]]" id="qrcode" text="[[qrCode(user.login,user.secret)]]" size="[[qrCodeWidth]]" ecl="H"></qrcode-printer>
				</div>
			</div>
			<div class="buttons">
				<paper-button class="modal-button" dialog-dismiss="">[[localize('can','Cancel',language)]]</paper-button>
				<paper-button class="modal-button modal-button--save" autofocus="" on-tap="confirm">[[localize('save','Save',language)]]</paper-button>
			</div>
		</paper-dialog>
`;
  }

  static get is() {
      return 'ht-my-profile';
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
          hcp: {
              type: Object,
              value: null
          },
          qrCodeWidth: {
              type: Number,
              value: 0
          },
          opened: {
              type: Boolean,
              value: false
          },
          verbose: {
              type: Boolean,
              value: true
          },
          userPassword: {
              type: String,
              value: null
          },
          userConfirmation: {
              type: String,
              value: null
          }
      };
	}

  static get observers() {
      return ['apiReady(api,user,opened)', '_userChanged(user)'];
	}

  ready() {
      super.ready();
      this.addEventListener('iron-resize', () => this.onWidthChange());
	}

  _userChanged(user) {
	}

  _mismatch(a, b) {
	    return a && a !== b
	}

  _tooShort(a) {
      return a && a.length < 8
  }

  apiReady() {
      if (this.user && this.api && this.opened) {
          this.api.hcparty().getHealthcareParty(this.user.healthcarePartyId).then(hcp => {
              if (!hcp.languages || !hcp.languages.length) {
                  hcp.languages = ['en'];
              }
              this.set('hcp', hcp);
          });
      }
  }

  qrCode(login, secret) {
      return login && secret ? `otpauth://totp/${login}:iCure-cloud?secret=${secret}&issuer=icure-cloud` : '';
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
      this.set('qrCodeWidth', Math.max(Math.min(offsetWidth / 2 - 64, 280), 120));
	}

  open() {
      this.$.dialog.open();
	}

  close() {
      this.$.dialog.close();
	}

  confirm() {
	    if ((!this.userPassword || this.userPassword.length > 7) && this.userPassword === this.userConfirmation) {
	        this.user.passwordHash = this.userPassword || this.user.passwordHash
          this.api.user().modifyUser(this.user).then(
              user => this.dispatchEvent(new CustomEvent('user-saved', {detail: user, bubbles: true, composed: true}))
          ).then(() => this.api.hcparty().modifyHealthcareParty(this.hcp)).finally(() => this.$.dialog.close())
      }
	}
}

customElements.define(HtExportKey.is, HtExportKey);

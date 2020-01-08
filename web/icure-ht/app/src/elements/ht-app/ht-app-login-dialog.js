//noinspection JSUnusedGlobalSymbols
import {PolymerElement, html} from '@polymer/polymer';
class HtAppLoginDialog extends TkLocalizerMixin(PolymerElement) {
  static get template() {
    return html`
		<style>
			paper-dialog{
				border-radius:2px;
			}
			.top-gradient{
				line-height:0;
				font-size:0;
				display:block;
				background: linear-gradient(90deg, var(--app-secondary-color-dark), var(--app-secondary-color));
				height:10px;
				position:relative;
				top:0;
				left:0;
				right:0;
				margin:0;
				border-radius:2px 2px 0 0;
			}
			paper-input{
				--paper-input-container-focus-color: var(--app-secondary-color-dark);
				--paper-input-container-invalid-color: var(--app-error-color);
			}
			paper-button{
				margin:2em auto 0 auto;
				color:var(--app-text-color-light);
				background:var(--app-secondary-color);
				--paper-button-ink-color: var(--app-secondary-color-dark);
				@apply --shadow-elevation-2dp;
				align-self:flex-end;
			}

			.flex{
				display:flex;
				flex-direction: column;
				justify-content: center!important;
			}

			.message.error{
				color: var(--app-error-color);
			}

		</style>

		<paper-dialog id="loginDialog" opened="{{opened}}" modal="">
			<div class="top-gradient">&nbsp;</div>
			<div style="text-align: center;"><!--img src="images/logo.png" style="width: 100px;" /--><h1>[[localize('ht','HT',language)]]</h1></div>
			<div hidden\$="{{!credentials.error}}" class="message error">
				[[localize('inv_use_or_pas','Invalid username or password.',language)]]
			</div>
			<div hidden\$="{{!credentials.logout}}" class="message">
				[[localize('you_hav_bee_log_out','You have been logged out.',language)]]
			</div>
			<form is="form" id="login-form">
				<div class="layout vertical center flex">
					<paper-input label="Username" name="username" always-float-label="true" value="{{credentials.username}}" invalid\$="{{credentials.error}}"></paper-input>
					<paper-input label="Password" name="password" type="password" always-float-label="true" value="{{credentials.password}}" on-keydown="checkForEnter" invalid\$="{{credentials.error}}"></paper-input>
					<paper-input label="2FA (optional)" name="twofa" always-float-label="true" value="{{credentials.twofa}}" on-keydown="checkForEnter"></paper-input>
					<paper-input label="Ehealth password (optional)" name="password" type="password" always-float-label="true" value="{{credentials.ehpassword}}" on-keydown="checkForEnter"></paper-input>
					<paper-button raised="true" id="submitButton" type="submit" on-click="login" autofocus="">[[localize('log_in','Log in',language)]]</paper-button>
				</div>
			</form>
		</paper-dialog>
`;
  }

  static get is() {
      return "ht-app-login-dialog";
	}

  static get properties() {
      return {
          credentials: {
              type: Object
          },
          opened: {
              type: Boolean,
              value: false,
              notify: true
          }
      };
	}

  constructor() {
      super();
	}

  login() {
      this.dispatchEvent(new CustomEvent('login', { detail: { credentials: this.credentials }, bubbles: true, composed: true }))
	}

  checkForEnter(e) {
      // check if 'enter' was pressed
      if (e.keyCode === 13) {
          this.dispatchEvent(new CustomEvent('login', { detail: { credentials: this.credentials }, bubbles: true, composed: true }))
      }
	}
}

customElements.define(HtAppLoginDialog.is, HtAppLoginDialog);
